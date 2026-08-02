package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.discord.TokenOtpDiscord;
import br.com.Vestibuline.domain.discord.TokenOtpDiscordRepository;
import br.com.Vestibuline.domain.discord.validacoes.ValidadorSincronizacaoDiscord;
import br.com.Vestibuline.domain.usuario.Usuario;
import br.com.Vestibuline.domain.usuario.UsuarioRepository;
import br.com.Vestibuline.domain.usuario.validacoes.ValidadorUsuario;
import br.com.Vestibuline.exception.RegraDeNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DiscordSyncService - Testes Unitários")
class DiscordSyncServiceTest {

    @Mock private TokenOtpDiscordRepository tokenRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ValidadorUsuario validadorUsuario;
    @Mock private ValidadorSincronizacaoDiscord validadorDiscord;

    private DiscordSyncService service;

    private static final Pattern FORMATO_TOKEN = Pattern.compile("^VEST-[A-Z2-9]{5}$");

    @BeforeEach
    void setUp() {
        service = new DiscordSyncService(tokenRepository, usuarioRepository, validadorUsuario, validadorDiscord);
    }

    @Test
    @DisplayName("gerarTokenSincronizacao: gera token no formato esperado quando usuário não tem Discord vinculado")
    void gerarToken_geraTokenValido_quandoUsuarioSemDiscord() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        doNothing().when(validadorUsuario).validarExistencia(usuarioId);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(tokenRepository.findByUsuarioId(usuarioId)).thenReturn(Optional.empty());

        String token = service.gerarTokenSincronizacao(usuarioId);

        assertThat(token).matches(FORMATO_TOKEN);
        verify(tokenRepository).save(any(TokenOtpDiscord.class));
    }

    @Test
    @DisplayName("gerarTokenSincronizacao: invalida e remove token anterior antes de gerar um novo")
    void gerarToken_removeTokenAnterior() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        TokenOtpDiscord tokenAntigo = new TokenOtpDiscord(usuario, "VEST-AAAAA");

        doNothing().when(validadorUsuario).validarExistencia(usuarioId);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(tokenRepository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(tokenAntigo));

        service.gerarTokenSincronizacao(usuarioId);

        verify(tokenRepository).delete(tokenAntigo);
        verify(tokenRepository).save(any(TokenOtpDiscord.class));
    }

    @Test
    @DisplayName("gerarTokenSincronizacao: lança RegraDeNegocioException se usuário já tem Discord vinculado")
    void gerarToken_lancaExcecao_quandoJaVinculado() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setDiscordId("123456789012345678");

        doNothing().when(validadorUsuario).validarExistencia(usuarioId);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> service.gerarTokenSincronizacao(usuarioId))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("já possui uma conta do Discord vinculada");

        verify(tokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("vincularContaDiscord: vincula com sucesso quando o consumo atômico do token funciona")
    void vincular_sucesso_quandoConsumoAtomicoFunciona() {
        String token = "VEST-AAAAA";
        String discordId = "123456789012345678";
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        TokenOtpDiscord tokenOtp = new TokenOtpDiscord(usuario, token);

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(tokenOtp));
        doNothing().when(validadorDiscord).validar(tokenOtp, discordId);
        when(tokenRepository.marcarComoUsado(token)).thenReturn(1);

        Usuario resultado = service.vincularContaDiscord(token, discordId);

        assertThat(resultado.getDiscordId()).isEqualTo(discordId);
        verify(tokenRepository).marcarComoUsado(token);
    }

    @Test
    @DisplayName("vincularContaDiscord: condição de corrida - se o UPDATE atômico não afeta linhas, rejeita a segunda tentativa")
    void vincular_lancaExcecao_quandoTokenJaConsumidoConcorrentemente() {
        String token = "VEST-AAAAA";
        String discordId = "123456789012345678";
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        TokenOtpDiscord tokenOtp = new TokenOtpDiscord(usuario, token);

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(tokenOtp));
        doNothing().when(validadorDiscord).validar(tokenOtp, discordId);
        // Simula outra requisição concorrente já ter consumido o token entre a leitura e o UPDATE.
        when(tokenRepository.marcarComoUsado(token)).thenReturn(0);

        assertThatThrownBy(() -> service.vincularContaDiscord(token, discordId))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("já foi utilizado");

        assertThat(usuario.getDiscordId()).isNull();
    }

    @Test
    @DisplayName("vincularContaDiscord: lança RegraDeNegocioException se o token não existir")
    void vincular_lancaExcecao_quandoTokenInexistente() {
        when(tokenRepository.findByToken("VEST-ZZZZZ")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.vincularContaDiscord("VEST-ZZZZZ", "123456789012345678"))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Token inválido ou inexistente");

        verify(tokenRepository, never()).marcarComoUsado(anyString());
    }
}
