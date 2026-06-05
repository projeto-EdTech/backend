package br.com.Vestibuline.controller;

import br.com.Vestibuline.domain.materia.dto.PerformanceMateriaProjection;
import br.com.Vestibuline.domain.usuario.dto.StatsGeralDTO;
import br.com.Vestibuline.service.UsuarioStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios/{usuarioId}/estatisticas")
@RequiredArgsConstructor
public class UsuarioStatsController {

    private final UsuarioStatsService service;

    @GetMapping("/geral")
    public ResponseEntity<StatsGeralDTO> obterStatsGeral(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(service.calcularStatsGeral(usuarioId));
    }

    @GetMapping("/performance-materia")
    public ResponseEntity<List<PerformanceMateriaProjection>> obterPerformanceMateria(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(service.calcularPerformancePorMateria(usuarioId));
    }
}
