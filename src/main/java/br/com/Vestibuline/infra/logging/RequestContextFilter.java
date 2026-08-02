package br.com.Vestibuline.infra.logging;

import br.com.Vestibuline.domain.usuario.Usuario;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Dá contexto a todos os logs de uma requisição (requestId + usuário, quando autenticado) via
 * MDC, e registra uma linha de acesso estruturada por requisição (método, path, status, duração).
 * Roda antes de tudo (incluindo o filtro de segurança) para que o requestId esteja disponível
 * em qualquer log emitido durante o processamento — inclusive em erros 401/403/429/500.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestContextFilter extends OncePerRequestFilter {

    private static final Logger accessLog = LoggerFactory.getLogger("br.com.Vestibuline.access");
    private static final String MDC_REQUEST_ID = "requestId";
    private static final String MDC_USUARIO_ID = "usuarioId";
    private static final String HEADER_REQUEST_ID = "X-Request-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString();
        long inicio = System.currentTimeMillis();

        MDC.put(MDC_REQUEST_ID, requestId);
        response.setHeader(HEADER_REQUEST_ID, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            popularUsuarioNoMdc();
            long duracaoMs = System.currentTimeMillis() - inicio;
            accessLog.info("{} {} -> {} ({}ms)", request.getMethod(), request.getRequestURI(),
                    response.getStatus(), duracaoMs);
            MDC.clear();
        }
    }

    private void popularUsuarioNoMdc() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Usuario usuario) {
            MDC.put(MDC_USUARIO_ID, usuario.getId().toString());
        }
    }
}
