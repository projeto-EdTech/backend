package br.com.Vestibuline.controller;

import br.com.Vestibuline.domain.materia.dto.PerformanceMateriaProjection;
import br.com.Vestibuline.domain.usuario.Usuario;
import br.com.Vestibuline.domain.usuario.dto.StatsGeralDTO;
import br.com.Vestibuline.service.UsuarioStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/usuarios/estatisticas")
@RequiredArgsConstructor
public class UsuarioStatsController {

    private final UsuarioStatsService service;

    @GetMapping("/geral")
    public ResponseEntity<StatsGeralDTO> obterStatsGeral(@AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.ok(service.calcularStatsGeral(usuarioLogado.getId()));
    }

    @GetMapping("/performance-materia")
    public ResponseEntity<List<PerformanceMateriaProjection>> obterPerformanceMateria(@AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.ok(service.calcularPerformancePorMateria(usuarioLogado.getId()));
    }
}
