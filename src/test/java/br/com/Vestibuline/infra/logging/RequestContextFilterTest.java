package br.com.Vestibuline.infra.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RequestContextFilter - Testes Unitários")
class RequestContextFilterTest {

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;

    @AfterEach
    void limpar() {
        MDC.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("gera requestId, define no header de resposta e limpa o MDC ao final")
    void geraRequestId_defineHeader_eLimpaMdcAoFinal() throws Exception {
        RequestContextFilter filter = new RequestContextFilter();

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/planner/piores-materias");
        when(response.getStatus()).thenReturn(200);

        // Durante a chain, o requestId já deve estar populado no MDC.
        doAnswer(invocation -> {
            assertThat(MDC.get("requestId")).isNotBlank();
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setHeader(eq("X-Request-Id"), anyString());
        verify(filterChain).doFilter(request, response);
        assertThat(MDC.get("requestId")).isNull(); // limpo após a requisição
    }

    @Test
    @DisplayName("requestId é diferente a cada requisição")
    void requestId_eDiferenteACadaRequisicao() throws Exception {
        RequestContextFilter filter = new RequestContextFilter();
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/qualquer");
        when(response.getStatus()).thenReturn(200);

        var idsCapturados = new java.util.ArrayList<String>();
        doAnswer(invocation -> {
            idsCapturados.add(MDC.get("requestId"));
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilterInternal(request, response, filterChain);
        filter.doFilterInternal(request, response, filterChain);

        assertThat(idsCapturados).hasSize(2);
        assertThat(idsCapturados.get(0)).isNotEqualTo(idsCapturados.get(1));
    }
}
