package br.com.Vestibuline.domain.conteudo;

import br.com.Vestibuline.domain.materia.Materia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConteudoRepository extends JpaRepository<Conteudo, UUID> {
    boolean existsByNomeAndMateria(String nome_fundamento, Materia materia);

    Optional<Conteudo> findByNomeAndMateria(String nome_fundamento, Materia materia);

    boolean existsByNomeIgnoreCase(String nome);
}
