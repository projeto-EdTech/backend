package br.com.Vestibuline.domain.discord;

import br.com.Vestibuline.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "token_otp_discord")
@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class TokenOtpDiscord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Usuario usuario;

    private String token;

    @Column(name = "data_expiracao", nullable = false)
    private LocalDateTime dataExpiracao;

    @Column(name = "usado", nullable = false)
    private boolean tokenUsado;

    public boolean estaExpirado() {
        return LocalDateTime.now().isAfter(this.dataExpiracao);
    }

    public TokenOtpDiscord(UUID id, Usuario usuario, String token, LocalDateTime dataExpiracao, boolean tokenUsado) {
        this.id = id;
        this.usuario = usuario;
        this.token = token;
        this.dataExpiracao = dataExpiracao;
        this.tokenUsado = tokenUsado;
    }

    public TokenOtpDiscord(Usuario usuario, String token) {
        this.usuario = usuario;
        this.token = token;
        this.dataExpiracao = LocalDateTime.now().plusMinutes(5);
        this.tokenUsado = false;
    }
}
