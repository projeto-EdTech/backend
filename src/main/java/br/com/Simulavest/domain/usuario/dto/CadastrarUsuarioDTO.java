package br.com.Simulavest.domain.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CadastrarUsuarioDTO(
        @NotBlank
        String nome,
        @Email
        String email
) {}
