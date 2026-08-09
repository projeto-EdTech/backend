package br.com.Vestibuline.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InternalAccessFilter - Testes Unitários")
class InternalAccessFilterTest {

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;

    private InternalAccessFilter criarFiltro(String tokenConfigurado) {
        InternalAccessFilter filter = new InternalAccessFilter();
        ReflectionTestUtils.setField(filter, "tokenConfigurado", tokenConfigurado);
        return filter;
    }

    @Test
    @DisplayName("token não configurado (vazio) -> 404, mesmo sem header algum (fail-closed)")
    void tokenNaoConfigurado_retorna404() throws Exception {
        InternalAccessFilter filter = criarFiltro("");
        when(request.getRequestURI()).thenReturn("/internal/logs-erro");

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(404);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("token configurado mas header ausente/errado -> 404")
    void tokenErrado_retorna404() throws Exception {
        InternalAccessFilter filter = criarFiltro("segredo-correto");
        when(request.getRequestURI()).thenReturn("/internal/logs-erro");
        when(request.getHeader("X-Internal-Token")).thenReturn("segredo-errado");

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(404);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("token correto -> segue a cadeia normalmente")
    void tokenCorreto_seguerCadeia() throws Exception {
        InternalAccessFilter filter = criarFiltro("segredo-correto");
        when(request.getRequestURI()).thenReturn("/internal/logs-erro");
        when(request.getHeader("X-Internal-Token")).thenReturn("segredo-correto");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(404);
    }

    @Test
    @DisplayName("rotas fora de /internal/** não são afetadas, mesmo sem token configurado")
    void rotasForaDoEscopo_naoSaoAfetadas() throws Exception {
        InternalAccessFilter filter = criarFiltro("");
        when(request.getRequestURI()).thenReturn("/planner/piores-materias");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(404);
    }
}
