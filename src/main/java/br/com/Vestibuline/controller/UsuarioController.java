package br.com.Vestibuline.controller;

import br.com.Vestibuline.domain.usuario.dto.AtualizarPerfilDTO;
import br.com.Vestibuline.domain.usuario.dto.InscricaoArtigoDTO;
import br.com.Vestibuline.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @PostMapping("/newsletter")
    public ResponseEntity<String> ativarNewsLetter(@RequestBody @Valid InscricaoArtigoDTO dto) {
        boolean sucesso = service.ativarNewsLetter(dto);
        if (sucesso) {
            return ResponseEntity.ok("Newsletter ativada com sucesso.");
        } else {
            return ResponseEntity.ok("Newsletter desativada com sucesso.");
        }
    }

    @PatchMapping("/{usuarioId}/perfil")
    public ResponseEntity<Void> atualizarPerfil(@PathVariable UUID usuarioId, @RequestBody @Valid AtualizarPerfilDTO dto) {

        service.atualizarInformacoesPerfil(usuarioId, dto);
        return ResponseEntity.noContent().build();
    }
}
