package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.usuario.Usuario;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class AuthService {

    private final UsuarioService usuarioService; // Usamos o Service, não o Repository
    private final TokenService tokenService;
    private final String googleClientId;

    // Injeção via Construtor (Boa prática: dispensa @Autowired nos campos)
    public AuthService(@Value("${infra.google.client-id}") String googleClientId,
                       TokenService tokenService,
                       UsuarioService usuarioService) {
        this.googleClientId = googleClientId;
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public String loginComGoogle(String tokenGoogle) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken token = verifier.verify(tokenGoogle);

            if (token == null) {
                throw new IllegalArgumentException("Token do Google inválido ou expirado.");
            }

            GoogleIdToken.Payload payload = token.getPayload();
            String email = payload.getEmail();
            String nome = (String) payload.get("name");

            // 1. Delegamos a regra de negócio (Upsert) para o UsuarioService
            Usuario usuario = usuarioService.buscarOuCriarViaGoogle(email, nome);

            // 2. Geramos o token com o usuário retornado (que já tem ID e está salvo)
            return tokenService.gerarToken(usuario);

        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Erro ao autenticar com Google", e);
        }
    }
}