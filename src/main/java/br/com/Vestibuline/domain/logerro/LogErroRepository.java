package br.com.Vestibuline.domain.logerro;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LogErroRepository extends JpaRepository<LogErro, UUID> {

    // Só reaproveita (incrementa) um registro ainda não resolvido. Se o erro já foi marcado
    // como CORRIGIDO/VALIDADO e voltar a ocorrer, cria uma linha nova (possível regressão).
    Optional<LogErro> findFirstByFingerprintAndStatusIn(String fingerprint, List<StatusIncidente> statusEmAberto);

    @Query("SELECT l FROM LogErro l WHERE " +
            "(:status IS NULL OR l.status = :status) AND " +
            "(:severidade IS NULL OR l.severidade = :severidade) AND " +
            "(:ambiente IS NULL OR l.ambiente = :ambiente)")
    Page<LogErro> buscarComFiltros(@Param("status") StatusIncidente status,
                                    @Param("severidade") Severidade severidade,
                                    @Param("ambiente") String ambiente,
                                    Pageable pageable);
}
