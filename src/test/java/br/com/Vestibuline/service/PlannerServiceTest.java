package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.planner.PlannerRepository;
import br.com.Vestibuline.domain.planner.dto.ConteudoDesempenhoDTO;
import br.com.Vestibuline.domain.planner.dto.MateriaDesempenhoDTO;
import br.com.Vestibuline.domain.planner.interfaces.PlannerProjection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.withSettings;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlannerService - Testes Unitários")
class PlannerServiceTest {

    @Mock
    private PlannerRepository plannerRepository;

    @InjectMocks
    private PlannerService plannerService;

    // -------------------------------------------------------------------------
    // Helpers para criar projections mockadas
    // -------------------------------------------------------------------------

    private PlannerProjection mockProjection(
            String materiaId, String materiaNome,
            long matTotal, long matErros, double matTaxa,
            String conteudoId, String conteudoNome,
            long conTotal, long conErros, double conTaxa
    ) {
        PlannerProjection p = mock(
                PlannerProjection.class,
                withSettings().lenient()
        );

        when(p.getMateriaId()).thenReturn(materiaId);
        when(p.getMateriaNome()).thenReturn(materiaNome);
        when(p.getMateriaTotalRespostas()).thenReturn(matTotal);
        when(p.getMateriaTotalErros()).thenReturn(matErros);
        when(p.getMateriaTaxaErro()).thenReturn(matTaxa);
        when(p.getConteudoId()).thenReturn(conteudoId);
        when(p.getConteudoNome()).thenReturn(conteudoNome);
        when(p.getConteudoTotalRespostas()).thenReturn(conTotal);
        when(p.getConteudoTotalErros()).thenReturn(conErros);
        when(p.getConteudoTaxaErro()).thenReturn(conTaxa);

        return p;
    }

    // -------------------------------------------------------------------------
    // Cenário 1 — retorno vazio
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar lista vazia quando não há respostas para o usuário")
    void deveRetornarListaVaziaQuandoSemRespostas() {
        UUID usuarioId = UUID.randomUUID();
        when(plannerRepository.findTop3MateriasComTop3Conteudos(usuarioId))
                .thenReturn(Collections.emptyList());

        List<MateriaDesempenhoDTO> resultado =
                plannerService.buscarPioresMateriasComConteudos(usuarioId);

        assertThat(resultado).isEmpty();
        verify(plannerRepository, times(1))
                .findTop3MateriasComTop3Conteudos(usuarioId);
    }

    // -------------------------------------------------------------------------
    // Cenário 2 — 1 matéria com 3 conteúdos
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve agrupar corretamente 3 conteúdos de uma mesma matéria")
    void deveAgruparConteudosDaMesmaMateria() {
        UUID usuarioId = UUID.randomUUID();
        String materiaId = UUID.randomUUID().toString();

        List<PlannerProjection> rows = List.of(
                mockProjection(materiaId, "Matemática", 30L, 24L, 80.0,
                        UUID.randomUUID().toString(), "Geometria Espacial", 10L, 9L, 90.0),
                mockProjection(materiaId, "Matemática", 30L, 24L, 80.0,
                        UUID.randomUUID().toString(), "Probabilidade", 10L, 8L, 80.0),
                mockProjection(materiaId, "Matemática", 30L, 24L, 80.0,
                        UUID.randomUUID().toString(), "Matrizes", 10L, 7L, 70.0)
        );

        when(plannerRepository.findTop3MateriasComTop3Conteudos(usuarioId)).thenReturn(rows);

        List<MateriaDesempenhoDTO> resultado =
                plannerService.buscarPioresMateriasComConteudos(usuarioId);

        assertThat(resultado).hasSize(1);

        MateriaDesempenhoDTO materia = resultado.get(0);
        assertThat(materia.materiaNome()).isEqualTo("Matemática");
        assertThat(materia.totalRespostas()).isEqualTo(30L);
        assertThat(materia.totalErros()).isEqualTo(24L);
        assertThat(materia.taxaErro()).isEqualTo(80.0);
        assertThat(materia.pioresConteudos()).hasSize(3);

        // Verifica ordem dos conteúdos (maior taxa de erro primeiro)
        assertThat(materia.pioresConteudos().get(0).conteudoNome()).isEqualTo("Geometria Espacial");
        assertThat(materia.pioresConteudos().get(1).conteudoNome()).isEqualTo("Probabilidade");
        assertThat(materia.pioresConteudos().get(2).conteudoNome()).isEqualTo("Matrizes");
    }

    // -------------------------------------------------------------------------
    // Cenário 3 — 3 matérias com 3 conteúdos cada
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar top 3 matérias, cada uma com top 3 conteúdos, na ordem correta")
    void deveRetornarTop3MateriasComTop3Conteudos() {
        UUID usuarioId = UUID.randomUUID();

        String mat1 = UUID.randomUUID().toString();
        String mat2 = UUID.randomUUID().toString();
        String mat3 = UUID.randomUUID().toString();

        List<PlannerProjection> rows = List.of(
                // Matéria 1 — pior (85%)
                mockProjection(mat1, "Física",     30L, 25L, 85.0, UUID.randomUUID().toString(), "Óptica",        10L, 10L, 100.0),
                mockProjection(mat1, "Física",     30L, 25L, 85.0, UUID.randomUUID().toString(), "Termodinâmica", 10L, 9L,   90.0),
                mockProjection(mat1, "Física",     30L, 25L, 85.0, UUID.randomUUID().toString(), "Ondas",         10L, 6L,   60.0),
                // Matéria 2 — segunda pior (75%)
                mockProjection(mat2, "Química",    40L, 30L, 75.0, UUID.randomUUID().toString(), "Estequiometria",15L, 13L,  86.0),
                mockProjection(mat2, "Química",    40L, 30L, 75.0, UUID.randomUUID().toString(), "Soluções",      15L, 11L,  73.0),
                mockProjection(mat2, "Química",    40L, 30L, 75.0, UUID.randomUUID().toString(), "Cinética",      10L, 6L,   60.0),
                // Matéria 3 — terceira pior (65%)
                mockProjection(mat3, "Matemática", 60L, 39L, 65.0, UUID.randomUUID().toString(), "Geometria",     20L, 16L,  80.0),
                mockProjection(mat3, "Matemática", 60L, 39L, 65.0, UUID.randomUUID().toString(), "Matrizes",      20L, 13L,  65.0),
                mockProjection(mat3, "Matemática", 60L, 39L, 65.0, UUID.randomUUID().toString(), "Logaritmos",    20L, 10L,  50.0)
        );

        when(plannerRepository.findTop3MateriasComTop3Conteudos(usuarioId)).thenReturn(rows);

        List<MateriaDesempenhoDTO> resultado =
                plannerService.buscarPioresMateriasComConteudos(usuarioId);

        assertThat(resultado).hasSize(3);

        // Ordem das matérias
        assertThat(resultado.get(0).materiaNome()).isEqualTo("Física");
        assertThat(resultado.get(1).materiaNome()).isEqualTo("Química");
        assertThat(resultado.get(2).materiaNome()).isEqualTo("Matemática");

        // Cada matéria tem exatamente 3 conteúdos
        resultado.forEach(m ->
                assertThat(m.pioresConteudos()).hasSize(3)
        );

        // Primeiro conteúdo de Física deve ser Óptica (100%)
        assertThat(resultado.get(0).pioresConteudos().get(0).conteudoNome()).isEqualTo("Óptica");

        // Primeiro conteúdo de Química deve ser Estequiometria (86%)
        assertThat(resultado.get(1).pioresConteudos().get(0).conteudoNome()).isEqualTo("Estequiometria");
    }

    // -------------------------------------------------------------------------
    // Cenário 4 — dados do conteúdo mapeados corretamente
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve mapear corretamente todos os campos do ConteudoDesempenhoDTO")
    void deveMapearCamposDoConteudoCorretamente() {
        UUID usuarioId = UUID.randomUUID();
        String materiaId  = UUID.randomUUID().toString();
        String conteudoId = UUID.randomUUID().toString();

        PlannerProjection row = mockProjection(
                materiaId, "Biologia", 20L, 16L, 80.0,
                conteudoId, "Genética", 20L, 16L, 80.0
        );

        when(plannerRepository.findTop3MateriasComTop3Conteudos(usuarioId))
                .thenReturn(List.of(row));

        List<MateriaDesempenhoDTO> resultado =
                plannerService.buscarPioresMateriasComConteudos(usuarioId);

        ConteudoDesempenhoDTO conteudo = resultado.get(0).pioresConteudos().get(0);

        assertThat(conteudo.conteudoId()).isEqualTo(conteudoId);
        assertThat(conteudo.conteudoNome()).isEqualTo("Genética");
        assertThat(conteudo.totalRespostas()).isEqualTo(20L);
        assertThat(conteudo.totalErros()).isEqualTo(16L);
        assertThat(conteudo.taxaErro()).isEqualTo(80.0);
    }

    // -------------------------------------------------------------------------
    // Cenário 5 — verifica que o repository é chamado com o UUID correto
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve chamar o repository com o usuarioId correto")
    void deveChamarRepositoryComUsuarioIdCorreto() {
        UUID usuarioId = UUID.randomUUID();
        when(plannerRepository.findTop3MateriasComTop3Conteudos(usuarioId))
                .thenReturn(Collections.emptyList());

        plannerService.buscarPioresMateriasComConteudos(usuarioId);

        verify(plannerRepository).findTop3MateriasComTop3Conteudos(usuarioId);
        verify(plannerRepository, never())
                .findTop3MateriasComTop3Conteudos(argThat(id -> !id.equals(usuarioId)));
    }
}