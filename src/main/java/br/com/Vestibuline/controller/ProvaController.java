package br.com.Vestibuline.controller;

import br.com.Vestibuline.domain.prova.dto.EscolhaProvaEAnoRequestDTO;
import br.com.Vestibuline.service.ProvaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prova")
public class ProvaController {

    @Autowired
    private ProvaService service;

    @PostMapping("instituicao")
    public ResponseEntity escolherProvaPorInstituicaoEAno(@RequestBody EscolhaProvaEAnoRequestDTO dto) {
        var prova = service.escolherProvaPorInstituicaoEAno(dto);
        return ResponseEntity.ok(prova);
    }
}
