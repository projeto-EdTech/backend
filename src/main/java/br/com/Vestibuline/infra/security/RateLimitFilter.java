package br.com.Vestibuline.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate limiting simples em memória (por instância) para rotas públicas de autenticação
 * que trocam um segredo por acesso a uma conta. Não substitui um limitador distribuído
 * (ex.: Redis/Bucket4j) caso a aplicação passe a rodar em múltiplas instâncias.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1) // depois do RequestContextFilter, para que 429 também carregue requestId
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Duration JANELA = Duration.ofMinutes(5);
    private static final int LIMITE_DISCORD_SYNC = 10;
    private static final int LIMITE_GOOGLE_LOGIN = 20;

    private final Map<String, Janela> contadores = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Integer limite = limiteParaRota(request);
        if (limite != null) {
            String chave = clientIp(request) + "|" + request.getRequestURI();
            if (!permitir(chave, limite)) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"status\":429,\"error\":\"Muitas Requisições\",\"message\":\"Muitas tentativas. Tente novamente em alguns minutos.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private Integer limiteParaRota(HttpServletRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return null;
        }
        String uri = request.getRequestURI();
        if (uri.equals("/auth/discord/sync")) {
            return LIMITE_DISCORD_SYNC;
        }
        if (uri.equals("/auth/google")) {
            return LIMITE_GOOGLE_LOGIN;
        }
        return null;
    }

    private boolean permitir(String chave, int limite) {
        Instant agora = Instant.now();
        Janela janela = contadores.compute(chave, (k, atual) -> {
            if (atual == null || Duration.between(atual.inicio, agora).compareTo(JANELA) > 0) {
                return new Janela(agora);
            }
            return atual;
        });
        return janela.contador.incrementAndGet() <= limite;
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static final class Janela {
        private final Instant inicio;
        private final AtomicInteger contador = new AtomicInteger(0);

        private Janela(Instant inicio) {
            this.inicio = inicio;
        }
    }
}
