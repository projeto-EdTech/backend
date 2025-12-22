package br.com.Simulavest.domain.alternativa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlternativaRepository extends JpaRepository<Alternativa, UUID> {
}
