package br.com.Vestibuline.domain.usuario;

import br.com.Vestibuline.domain.usuario.dto.RankDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.newsletter = :status WHERE u.email = :email")
    int atualizarStatusNewsletter(@Param("email") String email, @Param("status") boolean status);

    @Query("""
            SELECT u.email
            from Usuario u
            WHERE u.newsletter = TRUE
            """)
    List<String> buscarEmailsNewsletter();

    @Query("""
    SELECT new br.com.Vestibuline.domain.usuario.dto.RankDTO(
        0,
        u.nome,
        u.email,
        u.rankPoints,
        u.rank
    )
    FROM Usuario u
    ORDER BY u.rankPoints DESC
    """)
    List<RankDTO> findAllByOrderByRankPointsDesc();
}
