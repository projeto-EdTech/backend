package br.com.Simulavest.controller;

import br.com.Simulavest.domain.nota_corte.dto.NotaCorteInputDTO;
import br.com.Simulavest.domain.nota_corte.dto.NotaCorteResponseDTO;
import br.com.Simulavest.service.NotaCorteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nota-corte")
public class NotaCorteController {

    @Autowired
    private NotaCorteService service;

    @PostMapping("/importar")
    public ResponseEntity<String> importarNotas(@RequestBody @Valid NotaCorteInputDTO dto) {

        service.importarNotas(dto);

        return ResponseEntity.ok("Importação realizada com sucesso!");
    }

    @GetMapping("/media")
    public ResponseEntity<NotaCorteResponseDTO> buscarMediaCorte(
            @RequestParam String curso,
            @RequestParam(required = false) String sigla
    ) {
        var resultado = service.buscarNotasCorte(sigla, curso);
        return ResponseEntity.ok(resultado);
    }
}
