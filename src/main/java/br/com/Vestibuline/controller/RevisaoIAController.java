package br.com.Vestibuline.controller;

import br.com.Vestibuline.domain.materia.dto.MateriaRevisaoDTO;
import br.com.Vestibuline.domain.usuario.Usuario;
import br.com.Vestibuline.service.RevisaoIAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/revisao")
@RequiredArgsConstructor
public class RevisaoIAController {

    private final RevisaoIAService revisaoIAService;

    @GetMapping("/usuarios/revisao-ia")
    public ResponseEntity<List<MateriaRevisaoDTO>> getQuestoesParaRevisao(@AuthenticationPrincipal Usuario usuarioLogado) {
        List<MateriaRevisaoDTO> response = revisaoIAService.obterQuestoesParaRevisao(usuarioLogado.getId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/usuarios/questoes/{questaoId}/revisado")
    public ResponseEntity<Void> atualizarStatusRevisado(@AuthenticationPrincipal Usuario usuarioLogado, @PathVariable UUID questaoId) {
        revisaoIAService.marcarComoRevisado(usuarioLogado.getId(), questaoId);
        return ResponseEntity.noContent().build();
    }
}
