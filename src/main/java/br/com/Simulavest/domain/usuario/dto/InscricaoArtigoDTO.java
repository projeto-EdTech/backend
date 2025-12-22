package br.com.Simulavest.domain.usuario.dto;

import jakarta.validation.constraints.Email;

public record InscricaoArtigoDTO(
        @Email
        String email
) {}
