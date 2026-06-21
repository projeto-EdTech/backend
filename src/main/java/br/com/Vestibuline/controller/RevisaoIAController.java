package br.com.Vestibuline.controller;

import br.com.Vestibuline.domain.materia.dto.MateriaRevisaoDTO;
import br.com.Vestibuline.service.RevisaoIAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/revisao")
@RequiredArgsConstructor
public class RevisaoIAController {

    private final RevisaoIAService revisaoIAService;

    @GetMapping("/usuarios/{usuarioId}/revisao-ia")
    public ResponseEntity<List<MateriaRevisaoDTO>> getQuestoesParaRevisao(@PathVariable UUID usuarioId) {
        List<MateriaRevisaoDTO> response = revisaoIAService.obterQuestoesParaRevisao(usuarioId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/usuarios/{usuarioId}/questoes/{questaoId}/revisado")
    public ResponseEntity<Void> atualizarStatusRevisado(@PathVariable UUID usuarioId, @PathVariable UUID questaoId) {
        revisaoIAService.marcarComoRevisado(usuarioId, questaoId);
        return ResponseEntity.noContent().build();
    }
}
