package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.planner.PlannerRepository;
import br.com.Vestibuline.domain.planner.dto.ConteudoDesempenhoDTO;
import br.com.Vestibuline.domain.planner.dto.MateriaDesempenhoDTO;
import br.com.Vestibuline.domain.planner.interfaces.PlannerProjection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PlannerService {

    private final PlannerRepository plannerRepository;

    public PlannerService(PlannerRepository plannerRepository) {
        this.plannerRepository = plannerRepository;
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
