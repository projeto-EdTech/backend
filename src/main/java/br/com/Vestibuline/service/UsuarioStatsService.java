package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.historico.HistoricoRepository;
import br.com.Vestibuline.domain.materia.dto.PerformanceMateriaProjection;
import br.com.Vestibuline.domain.usuario.dto.StatsGeralDTO;
import br.com.Vestibuline.domain.usuario.validacoes.ValidadorUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioStatsService {

    private final HistoricoRepository historicoRepository;
    private final ValidadorUsuario validadorUsuario;

    public StatsGeralDTO calcularStatsGeral(UUID usuarioId) {
        validadorUsuario.validarExistencia(usuarioId);
        StatsGeralDTO stats = historicoRepository.findStatsGeralByUsuarioId(usuarioId);

        if (stats == null || stats.totalSimulados() == 0) {
            return new StatsGeralDTO(0L, 0L, 0L, 0);
        }

        return stats;
    }

    public List<PerformanceMateriaProjection> calcularPerformancePorMateria(UUID usuarioId) {
        validadorUsuario.validarExistencia(usuarioId);
        return historicoRepository.findPerformancePorMateriaNativo(usuarioId);
    }
}
