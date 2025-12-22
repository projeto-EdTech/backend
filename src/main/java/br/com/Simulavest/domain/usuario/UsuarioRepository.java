package br.com.Simulavest.domain.usuario;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    boolean existsByEmail(String email);

    /**
     * Atualiza o campo "newsletter" para TRUE, com base no email do usuário
     * @param email - E-mail do usuário
     * @return - O número de linhas afetadas (deve ser 1 se o usuário foi encontrado)
     */
    @Modifying
    @Transactional
    @Query("""
            UPDATE Usuario u
            SET u.newsletter = TRUE
            WHERE u.email = :email
            """)
    int atualizarInscricaoArtigo(
            @Param("email") String email
    );

    @Query("""
            SELECT u.email
            from Usuario u
            WHERE u.newsletter = TRUE
            """)
    List<String> buscarEmailsNewsletter();
}
