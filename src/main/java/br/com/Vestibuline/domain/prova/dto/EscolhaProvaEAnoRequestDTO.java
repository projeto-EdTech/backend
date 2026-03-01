package br.com.Vestibuline.domain.prova.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EscolhaProvaEAnoRequestDTO(
        @NotNull(message = "A instituição é obrigatória")
        @NotBlank
        String instituicao,
        @NotNull(message = "O ano é obrigatório")
        int ano
) {
}
