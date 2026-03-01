package br.com.Vestibuline.domain.nota_corte;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface NotaCorteRepository extends JpaRepository<NotaCorte, UUID> {

    List<NotaCorte> findByInstituicaoSiglaIgnoreCaseAndAno(String sigla, Integer ano);

    @Query("SELECT n FROM NotaCorte n " +
            "WHERE LOWER(n.nomeCurso) LIKE LOWER(CONCAT('%', :curso, '%')) " +
            "AND (:sigla IS NULL OR n.instituicao.sigla = :sigla)")
    List<NotaCorte> listagemNotaCorte(
            @Param("curso") String curso,
            @Param("sigla") String sigla
    );

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM NotaCorte n WHERE n.ano < :anoLimite")
    void deletarNotasCorteAntiga(@Param("anoLimite") Integer anoLimite);
}
