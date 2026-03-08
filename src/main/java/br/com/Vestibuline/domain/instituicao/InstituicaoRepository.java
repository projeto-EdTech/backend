package br.com.Vestibuline.domain.instituicao;

import br.com.Vestibuline.domain.instituicao.dtos.InstituicaoNomeEIdDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InstituicaoRepository extends JpaRepository<Instituicao, UUID> {
    Optional<Instituicao> findBySigla(String s);

    @Query("SELECT new br.com.Vestibuline.domain.instituicao.dtos.InstituicaoNomeEIdDto(i.id, i.nome) FROM Instituicao i")
    List<InstituicaoNomeEIdDto> findAllNomeEId();

    boolean existsBySiglaIgnoreCase(String sigla);
}
