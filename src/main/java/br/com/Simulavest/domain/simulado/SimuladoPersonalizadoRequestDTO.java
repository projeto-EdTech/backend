package br.com.Simulavest.domain.simulado;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SimuladoPersonalizadoRequestDTO(

        @NotEmpty(message = "A lista de fundamentos não pode ser vazia.")
        List<String> fundamentos,

        @NotEmpty(message = "A sigla não pode ser vazia.")
        String sigla,

        @NotNull(message = "A quantidade de questões é obrigatória.")
        Integer quantidade_questoes
) {}
