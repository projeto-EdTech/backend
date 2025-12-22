package br.com.Simulavest.domain.artigo.repository;

import br.com.Simulavest.domain.artigo.ArtigoStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArtigoStatsRepository extends JpaRepository<ArtigoStats, UUID> {
}
