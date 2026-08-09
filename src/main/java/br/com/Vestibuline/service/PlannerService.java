package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.instituicao.Instituicao;
import br.com.Vestibuline.domain.instituicao.dtos.EstatisticaMateriaDto;
import br.com.Vestibuline.domain.planner.PlannerRepository;
import br.com.Vestibuline.domain.planner.dto.ConteudoDesempenhoDTO;
import br.com.Vestibuline.domain.planner.dto.ConteudoPlanoDTO;
import br.com.Vestibuline.domain.planner.dto.MateriaDesempenhoDTO;
import br.com.Vestibuline.domain.planner.dto.MateriaPlanoDTO;
import br.com.Vestibuline.domain.planner.interfaces.PlannerProjection;
import br.com.Vestibuline.domain.usuario.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlannerService {

    private final PlannerRepository plannerRepository;
    private final UsuarioService usuarioService;
    private final InstituicaoService instituicaoService;
    private final MateriaService materiaService;

    public PlannerService(PlannerRepository plannerRepository, UsuarioService usuarioService, InstituicaoService instituicaoService, MateriaService materiaService) {
        this.plannerRepository = plannerRepository;
        this.usuarioService = usuarioService;
        this.instituicaoService = instituicaoService;
        this.materiaService = materiaService;
    }

    /**
     * Retorna as top 3 matérias com pior desempenho proporcional do usuário,
     * cada uma com seus top 3 conteúdos de pior desempenho.
     *
     * @param usuarioId UUID do usuário autenticado
     * @return lista de no máximo 3 MateriaDesempenhoDTO, cada um com até 3 conteúdos
     */
    @Transactional(readOnly = true)
    public List<MateriaDesempenhoDTO> buscarPioresMateriasComConteudos(UUID usuarioId) {
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarioId);
        Instituicao instituicao = instituicaoService.buscarInstituicaoPorSigla(usuario.getInstituicao());
        var materias = materiaService.listarMaterias();

        List<PlannerProjection> rows = plannerRepository
                .findTop3MateriasComTop3Conteudos(usuarioId);

        if (rows.isEmpty()) {
            return Collections.emptyList();
        }

        /*
         * A query já retorna os dados ordenados por rank_materia / rank_conteudo.
         * Usamos LinkedHashMap para preservar a ordem de inserção (= ordem do rank).
         */
        Map<String, MateriaAgregada> materiaMap = new LinkedHashMap<>();

        for (PlannerProjection row : rows) {

            materiaMap.computeIfAbsent(row.getMateriaId(), id ->
                    new MateriaAgregada(
                            row.getMateriaId(),
                            row.getMateriaNome(),
                            row.getMateriaTotalRespostas(),
                            row.getMateriaTotalErros(),
                            row.getMateriaTaxaErro()
                    )
            ).addConteudo(new ConteudoDesempenhoDTO(
                    row.getConteudoId(),
                    row.getConteudoNome(),
                    row.getConteudoTotalRespostas(),
                    row.getConteudoTotalErros(),
                    row.getConteudoTaxaErro()
            ));
        }

        return materiaMap.values().stream()
                .map(MateriaAgregada::toDTO)
                .toList();
    }

    private static final int DEFAULT_TOP_CONTEUDOS_POR_MATERIA = 5;

    @Transactional(readOnly = true)
    public List<MateriaPlanoDTO> gerarPlanoDeEstudos(UUID usuarioId, int totalCardsPorDia) {
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarioId);
        Instituicao instituicao = instituicaoService.buscarInstituicaoPorSigla(usuario.getInstituicao());

        List<PlannerProjection> desempenho = plannerRepository
                .findDesempenhoCompletoPorUsuario(usuarioId, DEFAULT_TOP_CONTEUDOS_POR_MATERIA);

        if (desempenho.isEmpty()) {
            return Collections.emptyList();
        }

        // matéria -> (conteúdo normalizado -> % de incidência na prova alvo)
        Map<String, Map<String, Double>> estatisticasProva = instituicaoService
                .obterEstatisticasPorInstituicao(instituicao.getId())
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream().collect(Collectors.toMap(
                                dto -> normalizar(dto.conteudo()),
                                EstatisticaMateriaDto::percentual,
                                (a, b) -> a))
                ));

        Map<String, MateriaAgregadaPlano> materiaMap = new LinkedHashMap<>();
        for (PlannerProjection row : desempenho) {
            materiaMap.computeIfAbsent(row.getMateriaId(), id ->
                    new MateriaAgregadaPlano(row.getMateriaId(), row.getMateriaNome(), row.getMateriaTaxaErro())
            ).addConteudo(row.getConteudoNome(), row.getConteudoTaxaErro());
        }

        // peso da matéria: quanto pior o aproveitamento, maior a fatia de cards
        double somaTaxaErroMaterias = materiaMap.values().stream()
                .mapToDouble(m -> Math.max(m.taxaErroMateria, 0.01))
                .sum();

        List<MateriaPlanoDTO> plano = new ArrayList<>();

        for (MateriaAgregadaPlano materia : materiaMap.values()) {
            double pesoMateria = Math.max(materia.taxaErroMateria, 0.01) / somaTaxaErroMaterias;
            int cardsMateria = (int) Math.round(pesoMateria * totalCardsPorDia);

            Map<String, Double> percentuaisProva = estatisticasProva
                    .getOrDefault(materia.materiaNome, Collections.emptyMap());

            // peso do conteúdo = erro do usuário * (1 + incidência na prova)
            List<Double> pesosConteudo = new ArrayList<>();
            double somaPesosConteudo = 0;
            for (ConteudoDesempenho conteudo : materia.conteudos) {
                double percentualProva = percentuaisProva.getOrDefault(normalizar(conteudo.nome()), 0.0);
                double peso = Math.max(conteudo.taxaErro(), 0.01) * (1 + percentualProva / 100.0);
                pesosConteudo.add(peso);
                somaPesosConteudo += peso;
            }

            List<ConteudoPlanoDTO> conteudosPlano = new ArrayList<>();
            for (int i = 0; i < materia.conteudos.size(); i++) {
                ConteudoDesempenho conteudo = materia.conteudos.get(i);
                double percentualProva = percentuaisProva.getOrDefault(normalizar(conteudo.nome()), 0.0);
                double peso = pesosConteudo.get(i);
                double proporcao = somaPesosConteudo == 0 ? 0 : peso / somaPesosConteudo;
                int cardsConteudo = (int) Math.round(proporcao * cardsMateria);

                conteudosPlano.add(new ConteudoPlanoDTO(
                        conteudo.nome(), conteudo.taxaErro(), percentualProva, peso, cardsConteudo));
            }

            plano.add(new MateriaPlanoDTO(
                    materia.materiaId, materia.materiaNome, materia.taxaErroMateria, cardsMateria, conteudosPlano));
        }

        return plano;
    }

    private String normalizar(String texto) {
        return texto == null ? "" : texto.trim().toLowerCase();
    }

    private static final class MateriaAgregadaPlano {
        private final String materiaId;
        private final String materiaNome;
        private final double taxaErroMateria;
        private final List<ConteudoDesempenho> conteudos = new ArrayList<>();

        MateriaAgregadaPlano(String materiaId, String materiaNome, Double taxaErroMateria) {
            this.materiaId = materiaId;
            this.materiaNome = materiaNome;
            this.taxaErroMateria = taxaErroMateria != null ? taxaErroMateria : 0.0;
        }

        void addConteudo(String nome, Double taxaErro) {
            conteudos.add(new ConteudoDesempenho(nome, taxaErro != null ? taxaErro : 0.0));
        }
    }

    private record ConteudoDesempenho(String nome, double taxaErro) {}

    // -------------------------------------------------------------------------
    // Classe auxiliar interna (não exposta para fora do service)
    // -------------------------------------------------------------------------

    private static final class MateriaAgregada {

        private final String materiaId;
        private final String materiaNome;
        private final long   totalRespostas;
        private final long   totalErros;
        private final double taxaErro;
        private final List<ConteudoDesempenhoDTO> conteudos = new ArrayList<>();

        MateriaAgregada(String materiaId, String materiaNome,
                        Long totalRespostas, Long totalErros, Double taxaErro) {
            this.materiaId      = materiaId;
            this.materiaNome    = materiaNome;
            this.totalRespostas = totalRespostas != null ? totalRespostas : 0L;
            this.totalErros     = totalErros     != null ? totalErros     : 0L;
            this.taxaErro       = taxaErro       != null ? taxaErro       : 0.0;
        }

        void addConteudo(ConteudoDesempenhoDTO dto) {
            conteudos.add(dto);
        }

        MateriaDesempenhoDTO toDTO() {
            return new MateriaDesempenhoDTO(
                    materiaId,
                    materiaNome,
                    totalRespostas,
                    totalErros,
                    taxaErro,
                    List.copyOf(conteudos)
            );
        }
    }
}
