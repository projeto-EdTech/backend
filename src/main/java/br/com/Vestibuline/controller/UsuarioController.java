package br.com.Vestibuline.controller;

import br.com.Vestibuline.domain.usuario.dto.InscricaoArtigoDTO;
import br.com.Vestibuline.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
