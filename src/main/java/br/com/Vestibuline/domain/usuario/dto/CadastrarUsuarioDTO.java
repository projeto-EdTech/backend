package br.com.Vestibuline.domain.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CadastrarUsuarioDTO(
        @NotBlank
        String nome,
        @Email
        String email
) {}
