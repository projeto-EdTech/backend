package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.usuario.TipoUsuario;
import br.com.Vestibuline.domain.usuario.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TokenService - Testes Unitários")
class TokenServiceTest {

    private static final String SECRET_FORTE = "uma-chave-secreta-com-pelo-menos-32-bytes-de-entropia";

    private TokenService criarComSecret(String secret) {
        TokenService service = new TokenService();
        ReflectionTestUtils.setField(service, "secret", secret);
        return service;
    }

    @Test
    @DisplayName("validarSecret: não lança exceção quando o segredo tem >= 32 bytes")
    void validarSecret_naoLancaExcecao_quandoSecretForte() {
        TokenService service = criarComSecret(SECRET_FORTE);
        service.validarSecret();
    }

    @Test
    @DisplayName("validarSecret: lança IllegalStateException quando o segredo é nulo")
    void validarSecret_lancaExcecao_quandoSecretNulo() {
        TokenService service = criarComSecret(null);

        assertThatThrownBy(service::validarSecret)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("api.security.token.secret");
    }

    @Test
    @DisplayName("validarSecret: lança IllegalStateException quando o segredo é fraco/curto")
    void validarSecret_lancaExcecao_quandoSecretFraco() {
        TokenService service = criarComSecret("segredo-curto");

        assertThatThrownBy(service::validarSecret)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("gerarToken + getSubject: round-trip retorna o e-mail usado na geração")
    void gerarTokenEGetSubject_roundTrip() {
        TokenService service = criarComSecret(SECRET_FORTE);
        Usuario usuario = new Usuario("Fulano", "fulano@example.com", TipoUsuario.FREE);

        String token = service.gerarToken(usuario);
        String subject = service.getSubject(token);

        assertThat(subject).isEqualTo("fulano@example.com");
    }

    @Test
    @DisplayName("getSubject: lança RuntimeException para token malformado (não propaga stack trace de terceiros)")
    void getSubject_lancaExcecao_quandoTokenInvalido() {
        TokenService service = criarComSecret(SECRET_FORTE);

        assertThatThrownBy(() -> service.getSubject("token-invalido"))
                .isInstanceOf(RuntimeException.class);
    }
}
