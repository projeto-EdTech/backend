package br.com.Vestibuline.domain.artigo.repository;

import br.com.Vestibuline.domain.artigo.Artigo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArtigoRepository extends JpaRepository<Artigo, UUID> {
}
