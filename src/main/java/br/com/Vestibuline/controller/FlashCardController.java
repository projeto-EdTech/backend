package br.com.Vestibuline.controller;

import br.com.Vestibuline.domain.flashcard.dto.FlashCardDTO;
import br.com.Vestibuline.domain.usuario.Usuario;
import br.com.Vestibuline.service.FlashCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flashcards")
public class FlashCardController {

    @Autowired
    private FlashCardService service;

    @GetMapping("/recomendacao")
    public ResponseEntity<FlashCardDTO> getRecomendacao(@AuthenticationPrincipal Usuario usuarioLogado) {
        var recomendacao = service.gerarRecomendacao(usuarioLogado.getId());
        return ResponseEntity.ok(recomendacao);
    }
}
