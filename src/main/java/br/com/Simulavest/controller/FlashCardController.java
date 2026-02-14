package br.com.Simulavest.controller;

import br.com.Simulavest.domain.flashcard.dto.FlashCardDTO;
import br.com.Simulavest.service.FlashCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/flashcards")
public class FlashCardController {

    @Autowired
    private FlashCardService service;

    @GetMapping("/recomendacao")
    public ResponseEntity<FlashCardDTO> getRecomendacao(@RequestParam UUID userId) {
        var recomendacao = service.gerarRecomendacao(userId);
        return ResponseEntity.ok(recomendacao);
    }
}
