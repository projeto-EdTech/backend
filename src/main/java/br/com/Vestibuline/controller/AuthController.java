package br.com.Vestibuline.controller;

import br.com.Vestibuline.domain.discord.dto.DiscordSyncRequestDTO;
import br.com.Vestibuline.domain.discord.dto.DiscordSyncResponseDTO;
import br.com.Vestibuline.domain.usuario.Usuario;
import br.com.Vestibuline.service.AuthService;
import br.com.Vestibuline.service.DiscordSyncService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final DiscordSyncService discordSyncService;

    public AuthController(AuthService authService, DiscordSyncService discordSyncService) {
        this.authService = authService;
        this.discordSyncService = discordSyncService;
    }

    public record LoginGoogleDTO(@NotBlank String token) {}
    public record TokenResponseDTO(String token) {}

    @PostMapping("/google")
    public ResponseEntity<TokenResponseDTO> googleLogin(@RequestBody @Valid LoginGoogleDTO body) {
        String tokenJwt = authService.loginComGoogle(body.token());
        return ResponseEntity.ok(new TokenResponseDTO(tokenJwt));
    }

    @PostMapping("/discord/sync")
    public ResponseEntity<DiscordSyncResponseDTO> syncDiscord(@RequestBody @Valid DiscordSyncRequestDTO dto) {
        Usuario usuarioVinculado = discordSyncService.vincularContaDiscord(dto.token(), dto.discord_id());

        DiscordSyncResponseDTO response = new DiscordSyncResponseDTO(usuarioVinculado.getNome());
        return ResponseEntity.ok(response);
    }
}
