package br.com.Simulavest.controller;

import br.com.Simulavest.domain.simulado.dto.finalizado.SimuladoFinalizadoDTO;
import br.com.Simulavest.domain.simulado.dto.finalizado.SimuladoResultadoDTO;
import br.com.Simulavest.service.SimuladoFinalizadoService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/simulados")
public class SimuladoFinalizadoController {

    @Autowired
    private SimuladoFinalizadoService simuladoFinalizadoService;

    @PostMapping("/finalizar")
    @Transactional
    public ResponseEntity<SimuladoResultadoDTO> finalizarSimulado(@RequestBody @Valid SimuladoFinalizadoDTO dto) {

        SimuladoResultadoDTO resultado = simuladoFinalizadoService.processarSimulado(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }
}
