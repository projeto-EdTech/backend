package br.com.Simulavest.domain.prova;

import br.com.Simulavest.domain.instituicao.Instituicao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProvaRepository extends JpaRepository<Prova, UUID> {
    Optional<Prova> findProvaByInstituicaoAndAno(Instituicao instituicao, int ano);


}
