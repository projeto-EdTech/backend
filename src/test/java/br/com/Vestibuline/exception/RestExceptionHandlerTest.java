package br.com.Vestibuline.exception;

import br.com.Vestibuline.service.LogErroService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("RestExceptionHandler - Testes Unitários")
class RestExceptionHandlerTest {

    // LogErroService é mockado: aqui o foco é o mapeamento HTTP, não a persistência do log
    // de erro (isso é coberto por LogErroServiceTest).
    private final RestExceptionHandler handler = new RestExceptionHandler(mock(LogErroService.class));

    private WebRequest webRequest() {
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/teste");
        return request;
    }

    @Test
    @DisplayName("IllegalArgumentException mapeia para 400 com a mensagem original")
    void illegalArgument_mapeiaPara400() {
        var response = handler.handleIllegalArgument(new IllegalArgumentException("entrada inválida"), webRequest());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().toString()).contains("entrada inválida");
    }

    @Test
    @DisplayName("ResourceNotFoundException mapeia para 404")
    void resourceNotFound_mapeiaPara404() {
        var response = handler.handleResourceNotFound(new ResourceNotFoundException("não encontrado"), webRequest());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("RegraDeNegocioException mapeia para 400")
    void regraDeNegocio_mapeiaPara400() {
        var response = handler.handleRegraDeNegocio(new RegraDeNegocioException("regra violada"), webRequest());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("DataIntegrityViolationException mapeia para 409 sem vazar detalhes internos do banco")
    void dataIntegrityViolation_mapeiaPara409SemVazarDetalhes() {
        var response = handler.handleDataIntegrityViolation(
                new DataIntegrityViolationException("duplicate key value violates unique constraint \"uk_prova_instituicao_ano_dia\""),
                webRequest());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().toString()).doesNotContain("uk_prova_instituicao_ano_dia");
    }

    @Test
    @DisplayName("Exception genérica mapeia para 500 sem vazar a mensagem/stacktrace original")
    void excecaoGenerica_mapeiaPara500SemVazarDetalhes() {
        var response = handler.handleGeneral(
                new RuntimeException("detalhe interno sensível: senha=123"),
                webRequest());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().toString()).doesNotContain("senha=123");
    }
}
