package br.com.Vestibuline.controller;

import br.com.Vestibuline.domain.planner.dto.MateriaDesempenhoDTO;
import br.com.Vestibuline.domain.planner.dto.MateriaPlanoDTO;
import br.com.Vestibuline.domain.usuario.Usuario;
import br.com.Vestibuline.service.PlannerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @GetMapping("/piores-materias")
    public ResponseEntity<List<MateriaDesempenhoDTO>> getPioresMaterias(@AuthenticationPrincipal Usuario usuarioLogado) {
        List<MateriaDesempenhoDTO> resultado =
                plannerService.buscarPioresMateriasComConteudos(usuarioLogado.getId());

        if (resultado.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/plano-inteligente")
    public ResponseEntity<List<MateriaPlanoDTO>> getPlanoInteligente(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestParam(defaultValue = "50") int totalCards) {

        List<MateriaPlanoDTO> plano = plannerService.gerarPlanoDeEstudos(usuarioLogado.getId(), totalCards);

        if (plano.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(plano);
    }
}