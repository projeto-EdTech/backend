package br.com.Vestibuline.controller;

import br.com.Vestibuline.domain.usuario.TipoUsuario;
import br.com.Vestibuline.domain.usuario.Usuario;
import br.com.Vestibuline.domain.usuario.dto.AtualizarPerfilDTO;
import br.com.Vestibuline.infra.security.SecurityFilter;
import br.com.Vestibuline.service.DiscordSyncService;
import br.com.Vestibuline.service.LogErroService;
import br.com.Vestibuline.service.TokenService;
import br.com.Vestibuline.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UsuarioController não recebe mais o id do usuário por path/body — todas as operações usam
 * o id extraído do token JWT autenticado (@AuthenticationPrincipal), fechando estruturalmente
 * a classe de IDOR que existia quando o id vinha da URL.
 */
@WebMvcTest(
        controllers = UsuarioController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityFilter.class
        )
)
@DisplayName("UsuarioController - Testes de Integração Web")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private DiscordSyncService discordSyncService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private LogErroService logErroService; // satisfaz a dependência do RestExceptionHandler (@ControllerAdvice)

    private Authentication autenticacaoDe(Usuario usuario) {
        return new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
    }

    private Usuario usuarioComId(UUID id) {
        Usuario usuario = new Usuario("Fulano", "fulano@example.com", TipoUsuario.FREE);
        usuario.setId(id);
        return usuario;
    }

    @Test
    @DisplayName("PATCH /usuarios/perfil - sem autenticação retorna 401")
    void atualizarPerfil_semAutenticacao_retorna401() throws Exception {
        var dto = new AtualizarPerfilDTO("ENEM", "Medicina", "USP");

        mockMvc.perform(patch("/usuarios/perfil")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());

        verify(usuarioService, never()).atualizarInformacoesPerfil(any(), any());
    }

    @Test
    @DisplayName("PATCH /usuarios/perfil - atualiza usando o id do usuário autenticado (token), 204")
    void atualizarPerfil_usaIdDoTokenAutenticado() throws Exception {
        UUID idAutenticado = UUID.randomUUID();
        Usuario autenticado = usuarioComId(idAutenticado);

        var dto = new AtualizarPerfilDTO("ENEM", "Medicina", "USP");

        mockMvc.perform(patch("/usuarios/perfil")
                        .with(authentication(autenticacaoDe(autenticado)))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(usuarioService).atualizarInformacoesPerfil(eq(idAutenticado), any());
    }

    @Test
    @DisplayName("POST /usuarios/generate-token - gera token para o id do usuário autenticado (token), nunca outro")
    void generateToken_usaIdDoTokenAutenticado() throws Exception {
        UUID idAutenticado = UUID.randomUUID();
        Usuario autenticado = usuarioComId(idAutenticado);

        when(discordSyncService.gerarTokenSincronizacao(idAutenticado)).thenReturn("VEST-ABCDE");

        mockMvc.perform(post("/usuarios/generate-token")
                        .with(authentication(autenticacaoDe(autenticado))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("VEST-ABCDE"));

        verify(discordSyncService).gerarTokenSincronizacao(idAutenticado);
    }

    @Test
    @DisplayName("POST /usuarios/newsletter - usa o e-mail do usuário autenticado")
    void ativarNewsletter_usaEmailDoAutenticado() throws Exception {
        UUID idAutenticado = UUID.randomUUID();
        Usuario autenticado = usuarioComId(idAutenticado);

        when(usuarioService.ativarNewsLetter("fulano@example.com")).thenReturn(true);

        mockMvc.perform(post("/usuarios/newsletter")
                        .with(authentication(autenticacaoDe(autenticado))))
                .andExpect(status().isOk());

        verify(usuarioService).ativarNewsLetter("fulano@example.com");
    }
}
