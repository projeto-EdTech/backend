package br.com.Simulavest.domain.artigo.repository;

import br.com.Simulavest.domain.artigo.Artigo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

public interface ArtigoRepository extends JpaRepository<Artigo, UUID> {
}
