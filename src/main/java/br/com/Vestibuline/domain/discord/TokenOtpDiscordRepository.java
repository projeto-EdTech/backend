package br.com.Vestibuline.domain.discord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface TokenOtpDiscordRepository extends JpaRepository<TokenOtpDiscord, UUID> {

    Optional<TokenOtpDiscord> findByToken(String token);

    Optional<TokenOtpDiscord> findByUsuarioId(UUID usuarioId);

    // UPDATE atômico condicionado a "ainda não usado": evita que duas requisições
    // concorrentes consumam o mesmo OTP (retorna 0 linhas afetadas se já usado).
    @Modifying
    @Query("UPDATE TokenOtpDiscord t SET t.tokenUsado = true WHERE t.token = :token AND t.tokenUsado = false")
    int marcarComoUsado(@Param("token") String token);

    @Modifying
    @Query("DELETE FROM TokenOtpDiscord t WHERE t.tokenUsado = true OR t.dataExpiracao < :limite")
    int deletarUsadosOuExpirados(@Param("limite") LocalDateTime limite);
}
