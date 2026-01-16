package br.com.Simulavest.controller;

import br.com.Simulavest.domain.questao.dto.ContagemQuestaoRequestDTO;
import br.com.Simulavest.domain.simulado.dto.personalizado.SimuladoPersonalizadoDTO;
import br.com.Simulavest.domain.simulado.dto.personalizado.SimuladoPersonalizadoRequestDTO;
import br.com.Simulavest.service.SimuladoPersonalizadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/simulados")
@RequiredArgsConstructor
public class SimuladoPersonalizadoController {

    private final SimuladoPersonalizadoService service;

    @PostMapping("/personalizados/contagem")
    public ResponseEntity<Map<String, Long>> getContagemQuestoes(@RequestBody @Valid ContagemQuestaoRequestDTO dto) {
        long totalQuestoes = service.quantidadeQuestoes(dto);
        return ResponseEntity.ok(Map.of("totalQuestoes", totalQuestoes));
    }

    @PostMapping("/personalizado/iniciar")
    public ResponseEntity<List<SimuladoPersonalizadoDTO>> iniciarSimuladoPersonalizado(@RequestBody @Valid SimuladoPersonalizadoRequestDTO dto) {

        List<SimuladoPersonalizadoDTO> simulado = service.iniciarSimulado(dto);

        return ResponseEntity.ok(simulado);
    }
}