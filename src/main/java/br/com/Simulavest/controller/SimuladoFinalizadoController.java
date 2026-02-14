package br.com.Simulavest.controller;

import br.com.Simulavest.domain.historico.dto.HistoricoDTO;
import br.com.Simulavest.domain.resposta.dto.SimuladoInputDTO;
import br.com.Simulavest.service.HistoricoService;
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
    private HistoricoService service;

    @PostMapping("/finalizar")
    @Transactional
    public ResponseEntity<HistoricoDTO> finalizarSimulado(@RequestBody @Valid SimuladoInputDTO dto) {

        var historico = service.cadastrarHistorico(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(historico);
    }
}
