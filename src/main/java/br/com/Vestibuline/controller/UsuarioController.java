package br.com.Vestibuline.controller;

import br.com.Vestibuline.domain.usuario.Usuario;
import br.com.Vestibuline.domain.usuario.dto.AtualizarPerfilDTO;
import br.com.Vestibuline.service.DiscordSyncService;
import br.com.Vestibuline.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;
    private final DiscordSyncService discordSyncService;

    @PostMapping("/newsletter")
    public ResponseEntity<String> ativarNewsLetter(@AuthenticationPrincipal Usuario usuarioLogado) {
        boolean sucesso = service.ativarNewsLetter(usuarioLogado.getEmail());
        if (sucesso) {
            return ResponseEntity.ok("Newsletter ativada com sucesso.");
        } else {
            return ResponseEntity.ok("Newsletter desativada com sucesso.");
        }
    }

    @PatchMapping("/perfil")
    public ResponseEntity<Void> atualizarPerfil(@RequestBody @Valid AtualizarPerfilDTO dto,
                                                 @AuthenticationPrincipal Usuario usuarioLogado) {
        service.atualizarInformacoesPerfil(usuarioLogado.getId(), dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/generate-token")
    public ResponseEntity<Map<String, String>> generateDiscordToken(@AuthenticationPrincipal Usuario usuarioLogado) {
        String tokenGerado = discordSyncService.gerarTokenSincronizacao(usuarioLogado.getId());

        return ResponseEntity.ok(Map.of("token", tokenGerado));
    }
}
