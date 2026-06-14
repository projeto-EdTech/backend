package br.com.Vestibuline.controller;

import br.com.Vestibuline.domain.planner.dto.ConteudoDesempenhoDTO;
import br.com.Vestibuline.domain.planner.dto.MateriaDesempenhoDTO;
import br.com.Vestibuline.infra.security.SecurityFilter;
import br.com.Vestibuline.service.PlannerService;
import br.com.Vestibuline.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = PlannerController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityFilter.class  // exclui o filtro customizado que depende do TokenService
        )
)
@DisplayName("PlannerController - Testes de Integração Web")
class PlannerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlannerService plannerService;

    @MockitoBean
    private TokenService tokenService; // satisfaz dependência residual do SecurityFilter se ainda carregado

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private MateriaDesempenhoDTO buildMateria(String nome, double taxaErro,
                                              List<ConteudoDesempenhoDTO> conteudos) {
        return new MateriaDesempenhoDTO(
                UUID.randomUUID().toString(),
                nome,
                100L,
                Math.round(taxaErro),
                taxaErro,
                conteudos
        );
    }

    private ConteudoDesempenhoDTO buildConteudo(String nome, double taxaErro) {
        return new ConteudoDesempenhoDTO(
                UUID.randomUUID().toString(),
                nome,
                20L,
                Math.round(taxaErro * 20 / 100),
                taxaErro
        );
    }

    // -------------------------------------------------------------------------
    // Cenário 1 — 200 OK com dados
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /planner/piores-materias/{userid} - deve retornar 200 com lista de matérias")
    void deveRetornar200ComListaDeMaterias() throws Exception {
        UUID usuarioId = UUID.randomUUID();

        List<MateriaDesempenhoDTO> mockResult = List.of(
                buildMateria("Física", 85.0, List.of(
                        buildConteudo("Óptica", 90.0),
                        buildConteudo("Termodinâmica", 80.0),
                        buildConteudo("Ondas", 70.0)
                ))
        );

        when(plannerService.buscarPioresMateriasComConteudos(usuarioId))
                .thenReturn(mockResult);

        mockMvc.perform(get("/planner/piores-materias/{userid}", usuarioId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].materiaNome").value("Física"))
                .andExpect(jsonPath("$[0].taxaErro").value(85.0))
                .andExpect(jsonPath("$[0].pioresConteudos.length()").value(3))
                .andExpect(jsonPath("$[0].pioresConteudos[0].conteudoNome").value("Óptica"))
                .andExpect(jsonPath("$[0].pioresConteudos[0].taxaErro").value(90.0));
    }

    // -------------------------------------------------------------------------
    // Cenário 2 — 204 No Content quando lista vazia
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /planner/piores-materias/{userid} - deve retornar 204 quando sem dados")
    void deveRetornar204QuandoSemDados() throws Exception {
        UUID usuarioId = UUID.randomUUID();

        when(plannerService.buscarPioresMateriasComConteudos(usuarioId))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/planner/piores-materias/{userid}", usuarioId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isNoContent());
    }

    // -------------------------------------------------------------------------
    // Cenário 3 — 401 sem autenticação
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /planner/piores-materias/{userid} - deve retornar 401 sem token JWT")
    void deveRetornar401SemAutenticacao() throws Exception {
        UUID usuarioId = UUID.randomUUID();

        mockMvc.perform(get("/planner/piores-materias/{userid}", usuarioId))
                .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------------------------------------
    // Cenário 4 — estrutura completa do JSON (3 matérias x 3 conteúdos)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /planner/piores-materias/{userid} - deve retornar estrutura completa com 3 matérias")
    void deveRetornarEstruturaCompletaComTresMaterias() throws Exception {
        UUID usuarioId = UUID.randomUUID();

        List<MateriaDesempenhoDTO> mockResult = List.of(
                buildMateria("Física", 85.0, List.of(
                        buildConteudo("Óptica", 90.0),
                        buildConteudo("Termodinâmica", 80.0),
                        buildConteudo("Ondas", 70.0)
                )),
                buildMateria("Química", 75.0, List.of(
                        buildConteudo("Estequiometria", 86.0),
                        buildConteudo("Soluções", 73.0),
                        buildConteudo("Cinética", 60.0)
                )),
                buildMateria("Matemática", 65.0, List.of(
                        buildConteudo("Geometria", 80.0),
                        buildConteudo("Matrizes", 65.0),
                        buildConteudo("Logaritmos", 50.0)
                ))
        );

        when(plannerService.buscarPioresMateriasComConteudos(usuarioId))
                .thenReturn(mockResult);

        mockMvc.perform(get("/planner/piores-materias/{userid}", usuarioId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].materiaNome").value("Física"))
                .andExpect(jsonPath("$[0].pioresConteudos.length()").value(3))
                .andExpect(jsonPath("$[0].pioresConteudos[0].conteudoNome").value("Óptica"))
                .andExpect(jsonPath("$[1].materiaNome").value("Química"))
                .andExpect(jsonPath("$[1].pioresConteudos[0].conteudoNome").value("Estequiometria"))
                .andExpect(jsonPath("$[2].materiaNome").value("Matemática"))
                .andExpect(jsonPath("$[2].pioresConteudos[0].conteudoNome").value("Geometria"));
    }

    // -------------------------------------------------------------------------
    // Cenário 5 — todos os campos mapeados corretamente no JSON
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /planner/piores-materias/{userid} - deve mapear todos os campos corretamente no JSON")
    void deveMapearTodosOsCamposNoJson() throws Exception {
        UUID usuarioId   = UUID.randomUUID();
        String materiaId  = UUID.randomUUID().toString();
        String conteudoId = UUID.randomUUID().toString();

        MateriaDesempenhoDTO dto = new MateriaDesempenhoDTO(
                materiaId, "Biologia", 50L, 40L, 80.0,
                List.of(new ConteudoDesempenhoDTO(conteudoId, "Genética", 20L, 18L, 90.0))
        );

        when(plannerService.buscarPioresMateriasComConteudos(usuarioId))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/planner/piores-materias/{userid}", usuarioId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].materiaId").value(materiaId))
                .andExpect(jsonPath("$[0].materiaNome").value("Biologia"))
                .andExpect(jsonPath("$[0].totalRespostas").value(50))
                .andExpect(jsonPath("$[0].totalErros").value(40))
                .andExpect(jsonPath("$[0].taxaErro").value(80.0))
                .andExpect(jsonPath("$[0].pioresConteudos[0].conteudoId").value(conteudoId))
                .andExpect(jsonPath("$[0].pioresConteudos[0].conteudoNome").value("Genética"))
                .andExpect(jsonPath("$[0].pioresConteudos[0].totalRespostas").value(20))
                .andExpect(jsonPath("$[0].pioresConteudos[0].totalErros").value(18))
                .andExpect(jsonPath("$[0].pioresConteudos[0].taxaErro").value(90.0));
    }

    // -------------------------------------------------------------------------
    // Cenário 6 — UUID inválido no path retorna 400
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /planner/piores-materias/{userid} - deve retornar 400 para UUID inválido")
    void deveRetornar400ParaUuidInvalido() throws Exception {
        mockMvc.perform(get("/planner/piores-materias/nao-e-um-uuid")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isBadRequest());
    }

    // -------------------------------------------------------------------------
    // Cenário 7 — service é chamado com o UUID correto do path
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /planner/piores-materias/{userid} - deve chamar o service com o UUID do path")
    void deveChamarServiceComUuidDoPath() throws Exception {
        UUID usuarioId = UUID.randomUUID();

        when(plannerService.buscarPioresMateriasComConteudos(usuarioId))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/planner/piores-materias/{userid}", usuarioId)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))));

        verify(plannerService, times(1)).buscarPioresMateriasComConteudos(usuarioId);
    }
}