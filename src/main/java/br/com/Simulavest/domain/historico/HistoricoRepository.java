package br.com.Simulavest.domain.historico;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistoricoRepository extends JpaRepository<Historico, UUID> {
    List<Historico> findByUsuarioId(UUID usuarioId);
}
