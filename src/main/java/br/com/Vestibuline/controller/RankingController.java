package br.com.Vestibuline.controller;

import br.com.Vestibuline.domain.usuario.dto.RankDTO;
import br.com.Vestibuline.service.RankingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final RankingService service;

    public RankingController(RankingService service) {
        this.service = service;
    }

    @GetMapping
    public List<RankDTO> findAll() {
        return service.getRanking();
    }
}
