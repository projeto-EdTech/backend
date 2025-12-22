package br.com.Simulavest.domain.materia;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface MateriaRepository extends JpaRepository<Materia, UUID> {
    Optional<Materia> findByNome(String nome);

    Optional<Materia> findMateriaByNomeContainingIgnoreCase(String nome);
}