package br.com.Vestibuline.domain.prova.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EscolhaProvaEAnoRequestDTO(
        @NotNull(message = "A instituição é obrigatória")
        @NotBlank
        String instituicao,
        @NotNull(message = "O ano é obrigatório")
        int ano,
        @NotNull(message = "O dia é obrigatório")
        @Positive(message = "Dia deve ser maior que 0")
        int dia
) {
}
