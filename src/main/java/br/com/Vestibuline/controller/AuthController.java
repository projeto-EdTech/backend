package br.com.Vestibuline.controller;

import br.com.Vestibuline.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    public record LoginGoogleDTO(@NotBlank String token) {}
    public record TokenResponseDTO(String token) {}

    @PostMapping("/google")
    public ResponseEntity<TokenResponseDTO> googleLogin(@RequestBody @Valid LoginGoogleDTO body) {

        String tokenJwt = authService.loginComGoogle(body.token());

        return ResponseEntity.ok(new TokenResponseDTO(tokenJwt));
    }
}
