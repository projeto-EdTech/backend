package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.discord.TokenOtpDiscord;
import br.com.Vestibuline.domain.discord.TokenOtpDiscordRepository;
import br.com.Vestibuline.domain.discord.validacoes.ValidadorSincronizacaoDiscord;
import br.com.Vestibuline.domain.usuario.Usuario;
import br.com.Vestibuline.domain.usuario.UsuarioRepository;
import br.com.Vestibuline.domain.usuario.validacoes.ValidadorUsuario;
import br.com.Vestibuline.exception.RegraDeNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.UUID;

@Service
public class DiscordSyncService {

    private final TokenOtpDiscordRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;
    private  final ValidadorUsuario validadorUsuario;
    private final ValidadorSincronizacaoDiscord validadorDiscord;

    private static final String CARACTERES = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private final SecureRandom random = new SecureRandom();

    public DiscordSyncService(TokenOtpDiscordRepository tokenRepository, UsuarioRepository usuarioRepository, ValidadorUsuario validadorUsuario, ValidadorSincronizacaoDiscord validadorDiscord) {
        this.tokenRepository = tokenRepository;
        this.usuarioRepository = usuarioRepository;
        this.validadorUsuario = validadorUsuario;
        this.validadorDiscord = validadorDiscord;
    }

    @Transactional
    public String gerarTokenSincronizacao(UUID usuarioId) {
        validadorUsuario.validarExistencia(usuarioId);
        Usuario usuario = usuarioRepository.findById(usuarioId).get();

        if (usuario.getDiscordId() != null && !usuario.getDiscordId().isBlank()) {
            throw new RegraDeNegocioException("Este usuário já possui uma conta do Discord vinculada.");
        }

        tokenRepository.findByUsuarioId(usuarioId).ifPresent(token -> {
            tokenRepository.delete(token);
            tokenRepository.flush();
        });

        StringBuilder sb = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            sb.append(CARACTERES.charAt(random.nextInt(CARACTERES.length())));
        }
        String tokenFormatado = "VEST-" + sb.toString();

        TokenOtpDiscord novoToken = new TokenOtpDiscord(usuario, tokenFormatado);
        tokenRepository.save(novoToken);

        return tokenFormatado;
    }

    @Transactional
    public Usuario vincularContaDiscord(String token, String discordId) {
        TokenOtpDiscord tokenOtp = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RegraDeNegocioException("Token inválido ou inexistente."));

        validadorDiscord.validar(tokenOtp, discordId);

        // Consumo atômico: garante que, sob concorrência, só uma requisição vença a corrida pelo token.
        if (tokenRepository.marcarComoUsado(token) == 0) {
            throw new RegraDeNegocioException("Este token já foi utilizado.");
        }

        Usuario usuario = tokenOtp.getUsuario();
        usuario.setDiscordId(discordId);

        return usuario;
    }
}
