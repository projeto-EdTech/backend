package br.com.Simulavest.controller;

import br.com.Simulavest.domain.simulado.dto.mix.SimuladoMixRequestDTO;
import br.com.Simulavest.domain.simulado.dto.personalizado.SimuladoPersonalizadoDTO;
import br.com.Simulavest.service.SimuladoMixService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/simulados")
@RequiredArgsConstructor
public class SimuladoMixController {

    @Autowired
    private SimuladoMixService service;

    @PostMapping("/mix/iniciar")
    public ResponseEntity<List<SimuladoPersonalizadoDTO>> iniciarSimuladoMix(@RequestBody @Valid SimuladoMixRequestDTO dto) {

        List<SimuladoPersonalizadoDTO> simulado = service.iniciarSimuladoMix(
                dto
        );

        return ResponseEntity.ok(simulado);
    }
}
