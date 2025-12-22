package br.com.Simulavest.controller;

import br.com.Simulavest.domain.prova.dto.EscolhaProvaEAnoRequestDTO;
import br.com.Simulavest.service.ProvaService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prova")
public class ProvaController {

    @Autowired
    private ProvaService service;

    @GetMapping("instituicao")
    public ResponseEntity escolherProvaPorInstituicaoEAno(@RequestBody EscolhaProvaEAnoRequestDTO dto) {
        var prova = service.escolherProvaPorInstituicaoEAno(dto);
        return ResponseEntity.ok(prova);
    }
}
