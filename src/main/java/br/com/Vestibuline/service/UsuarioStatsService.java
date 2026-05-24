package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.historico.HistoricoRepository;
import br.com.Vestibuline.domain.materia.dto.PerformanceMateriaProjection;
import br.com.Vestibuline.domain.usuario.UsuarioRepository;
import br.com.Vestibuline.domain.usuario.dto.StatsGeralDTO;
import br.com.Vestibuline.domain.usuario.validation.UsuarioValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioStatsService {

    @Autowired
    private HistoricoRepository historicoRepository;
    private UsuarioValidator usuarioValidator;

    public StatsGeralDTO calcularStatsGeral(UUID usuarioId) {
        usuarioValidator.validarExistencia(usuarioId);
        StatsGeralDTO stats = historicoRepository.findStatsGeralByUsuarioId(usuarioId);

        if (stats == null || stats.totalSimulados() == 0) {
            return new StatsGeralDTO(0L, 0L, 0L, 0);
        }

        return stats;
    }

    public List<PerformanceMateriaProjection> calcularPerformancePorMateria(UUID usuarioId) {
        usuarioValidator.validarExistencia(usuarioId);
        return historicoRepository.findPerformancePorMateriaNativo(usuarioId);
    }
}
