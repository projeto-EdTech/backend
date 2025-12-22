package br.com.Simulavest.domain.simulado;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SimuladoMixRequestDTO(

        @NotBlank(message = "A sigla da instituição é obrigatória")
        String sigla,

        @NotNull(message = "A quantidade de questões é obrigatória")
        @Min(value = 1, message = "A quantidade mínima é 1 questão")
        Integer quantidade_questoes
) {
}
