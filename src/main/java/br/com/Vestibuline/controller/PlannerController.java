package br.com.Vestibuline.controller;

import br.com.Vestibuline.domain.planner.dto.MateriaDesempenhoDTO;
import br.com.Vestibuline.service.PlannerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/planner")
public class PlannerController {

    private final PlannerService plannerService;

    public PlannerController(PlannerService plannerService) {
        this.plannerService = plannerService;
    }

    /**
     * GET /planner/piores-materias
     *
     * Retorna as top 3 matérias com pior desempenho proporcional do usuário autenticado,
     * cada uma com seus top 3 conteúdos que mais precisam de atenção.
     *
     * Exemplo de resposta:
     * [
     *   {
     *     "materiaId": "...",
     *     "materiaNome": "Matemática",
     *     "totalRespostas": 40,
     *     "totalErros": 32,
     *     "taxaErro": 80.00,
     *     "pioresConteudos": [
     *       { "conteudoId": "...", "conteudoNome": "Geometria Espacial", "totalRespostas": 10, "totalErros": 9, "taxaErro": 90.00 },
     *       { "conteudoId": "...", "conteudoNome": "Probabilidade",      "totalRespostas": 15, "totalErros": 13, "taxaErro": 86.67 },
     *       { "conteudoId": "...", "conteudoNome": "Matrizes",           "totalRespostas": 15, "totalErros": 10, "taxaErro": 66.67 }
     *     ]
     *   },
     *   ...
     * ]
     */
    @GetMapping("/piores-materias/{userid}")
    public ResponseEntity<List<MateriaDesempenhoDTO>> getPioresMaterias(@PathVariable UUID userid) {
        List<MateriaDesempenhoDTO> resultado =
                plannerService.buscarPioresMateriasComConteudos(userid);

        if (resultado.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(resultado);
    }
}