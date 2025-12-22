package br.com.Simulavest.domain.gabarito;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GabaritoRepository extends JpaRepository<Gabarito, UUID> {
    List<Gabarito> findByUsuarioIdAndQuestaoProvaId(UUID usuarioId, UUID provaId);
}
