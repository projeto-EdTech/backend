package br.com.Vestibuline.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Protege as rotas /internal/** (hoje: consulta de logs de erro para devs) atrás de um token
 * compartilhado configurado via variável de ambiente — não é um mecanismo de autenticação por
 * usuário, é um cadeado simples para uma ferramenta interna.
 *
 * Fail-closed: se INTERNAL_ACCESS_TOKEN não estiver configurado, /internal/** fica
 * inacessível (404, sem revelar que a rota existe) em vez de aberto por engano.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2) // depois do RequestContextFilter e do RateLimitFilter
public class InternalAccessFilter extends OncePerRequestFilter {

    private static final String PREFIXO_PROTEGIDO = "/internal/";
    private static final String HEADER_TOKEN = "X-Internal-Token";

    @Value("${internal.access.token:}")
    private String tokenConfigurado;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURI().startsWith(PREFIXO_PROTEGIDO)) {
            String tokenRecebido = request.getHeader(HEADER_TOKEN);
            if (!acessoAutorizado(tokenRecebido)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean acessoAutorizado(String tokenRecebido) {
        if (tokenConfigurado == null || tokenConfigurado.isBlank() || tokenRecebido == null) {
            return false;
        }
        byte[] esperado = tokenConfigurado.getBytes(StandardCharsets.UTF_8);
        byte[] recebido = tokenRecebido.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(esperado, recebido);
    }
}
