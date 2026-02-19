package br.com.Simulavest.domain.alternativa.dto;

import java.util.UUID;

public record AlternativaSimuladoNormalDTO(
        UUID id,
        String letra,
        String textoAlternativa
) {}
