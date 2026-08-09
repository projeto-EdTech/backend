package br.com.Vestibuline.service; // Recomendação: Mover para br.com.Simulavest.infra.security

import br.com.Vestibuline.domain.usuario.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    private static final int SECRET_MIN_BYTES = 32; // 256 bits

    @Value("${api.security.token.secret}")
    private String secret;

    private static final String ISSUER = "Vestibuline-API";

    @PostConstruct
    void validarSecret() {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < SECRET_MIN_BYTES) {
            throw new IllegalStateException(
                    "api.security.token.secret ausente ou fraco: " +
                    "defina um valor com pelo menos " + SECRET_MIN_BYTES + " bytes (256 bits) de entropia."
            );
        }
    }

    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(usuario.getEmail())
                    .withClaim("id", usuario.getId().toString())
                    .withClaim("nome", usuario.getNome())
                    // Tratamento seguro para Enum nulo
                    .withClaim("tipo", usuario.getTipoUsuario() != null ? usuario.getTipoUsuario().toString() : "FREE")
                    .withClaim("newsletter", usuario.isNewsletter())
                    .withExpiresAt(gerarDataExpiracao())
                    .sign(algoritmo);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido ou expirado!");
        }
    }

    private Instant gerarDataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}