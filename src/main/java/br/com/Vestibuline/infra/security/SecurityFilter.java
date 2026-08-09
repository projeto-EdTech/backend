package br.com.Vestibuline.infra.security;

import br.com.Vestibuline.domain.usuario.Usuario;
import br.com.Vestibuline.domain.usuario.UsuarioRepository;
import br.com.Vestibuline.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var token = this.recoverToken(request);

        if (token != null) {
            try {
                var email = tokenService.getSubject(token);

                var usuarioOptional = repository.findByEmail(email);

                if (usuarioOptional.isPresent()) {
                    Usuario usuario = usuarioOptional.get();

                    var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    MDC.put("usuarioId", usuario.getId().toString());
                }
            } catch (RuntimeException ex) {
                // Token ausente/expirado/malformado: segue sem autenticação em vez de propagar
                // a exceção para fora do filtro (o que gerava HTTP 500 com stack trace).
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");

        if (authHeader == null) return null;

        return authHeader.replace("Bearer ", "").trim();
    }
}