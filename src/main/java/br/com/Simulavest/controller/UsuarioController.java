package br.com.Simulavest.controller;

import br.com.Simulavest.domain.usuario.Usuario;
import br.com.Simulavest.domain.usuario.dto.CadastrarUsuarioDTO;
import br.com.Simulavest.domain.usuario.dto.InscricaoArtigoDTO;
import br.com.Simulavest.service.UsuarioService;
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
            return ResponseEntity.badRequest().body("Não foi possível ativar a newsletter.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> cadastrarUsuario(@RequestBody @Valid CadastrarUsuarioDTO dados) {

        Usuario usuarioSalvo = service.cadastrar(dados);

        return ResponseEntity.ok("Usuário cadastrado com sucesso!");
    }
}
