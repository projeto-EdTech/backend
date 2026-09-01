package br.com.Vestibuline.infra.security;

import br.com.Vestibuline.domain.usuario.Usuario;
import br.com.Vestibuline.domain.usuario.UsuarioRepository;
import br.com.Vestibuline.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityFilter - Testes Unitários")
class SecurityFilterTest {

    @Mock private TokenService tokenService;
    @Mock private UsuarioRepository repository;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;

    @InjectMocks
    private SecurityFilter securityFilter;

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
        MDC.clear();
    }

    @Test
    @DisplayName("token ausente: segue a cadeia sem autenticar, sem erro")
    void semToken_seguerCadeiaSemAutenticar() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("token inválido/expirado: não propaga exceção, segue sem autenticar (evita 500 com stacktrace)")
    void tokenInvalido_naoPropagaExcecao() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token-invalido");
        when(tokenService.getSubject("token-invalido")).thenThrow(new RuntimeException("Token JWT inválido ou expirado!"));

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("token válido: autentica o usuário no contexto de segurança")
    void tokenValido_autenticaUsuario() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setEmail("fulano@example.com");

        when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");
        when(tokenService.getSubject("token-valido")).thenReturn("fulano@example.com");
        when(repository.findByEmail("fulano@example.com")).thenReturn(Optional.of(usuario));

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(usuario);
    }
}
