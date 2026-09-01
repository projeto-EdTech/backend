package br.com.Vestibuline.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RateLimitFilter - Testes Unitários")
class RateLimitFilterTest {

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;
    @Mock private PrintWriter writer;

    @Test
    @DisplayName("permite até o limite configurado e bloqueia com 429 a partir da requisição excedente")
    void bloqueiaComHttp429_apartirDoLimite() throws Exception {
        RateLimitFilter filter = new RateLimitFilter();

        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/auth/discord/sync");
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        when(response.getWriter()).thenReturn(writer);

        // limite configurado para /auth/discord/sync é 10 (ver RateLimitFilter.LIMITE_DISCORD_SYNC)
        for (int i = 0; i < 10; i++) {
            filter.doFilterInternal(request, response, filterChain);
        }
        verify(filterChain, times(10)).doFilter(request, response);

        // 11ª requisição na mesma janela deve ser bloqueada
        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(10)).doFilter(request, response); // não incrementou
        verify(response).setStatus(429);
    }

    @Test
    @DisplayName("rotas fora do rate limit (ex.: GET) seguem normalmente, sem contagem")
    void naoLimitaRotasForaDoEscopo() throws Exception {
        RateLimitFilter filter = new RateLimitFilter();

        when(request.getMethod()).thenReturn("GET");

        for (int i = 0; i < 50; i++) {
            filter.doFilterInternal(request, response, filterChain);
        }

        verify(filterChain, times(50)).doFilter(request, response);
        verify(response, never()).setStatus(429);
    }

    @Test
    @DisplayName("IPs diferentes têm contadores independentes")
    void ipsDiferentes_temContadoresIndependentes() throws Exception {
        RateLimitFilter filter = new RateLimitFilter();

        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/auth/google");

        when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        for (int i = 0; i < 20; i++) {
            filter.doFilterInternal(request, response, filterChain);
        }

        when(request.getRemoteAddr()).thenReturn("10.0.0.2");
        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(21)).doFilter(request, response);
        verify(response, never()).setStatus(429);
    }
}
