package br.com.Vestibuline.domain.discord.validacoes;

import br.com.Vestibuline.domain.discord.TokenOtpDiscord;
import br.com.Vestibuline.domain.usuario.UsuarioRepository;
import br.com.Vestibuline.exception.RegraDeNegocioException;
import org.springframework.stereotype.Component;

@Component
public class ValidadorSincronizacaoDiscord {

    private final UsuarioRepository repository;

    public ValidadorSincronizacaoDiscord(UsuarioRepository repository) {
        this.repository = repository;
    }

    public void validar(TokenOtpDiscord tokenOtp, String discordId) {
        if (tokenOtp.isTokenUsado()) {
            throw new RegraDeNegocioException("Este token já foi utilizado.");
        }

        if (tokenOtp.estaExpirado()) {
            throw new RegraDeNegocioException("Este token expirou. Gere um novo na plataforma Web.");
        }

        if (repository.existsByDiscordId(discordId)) {
            throw new RegraDeNegocioException("Esta conta do Discord já está vinculada a outro usuário cadastrado.");
        }
    }
}
