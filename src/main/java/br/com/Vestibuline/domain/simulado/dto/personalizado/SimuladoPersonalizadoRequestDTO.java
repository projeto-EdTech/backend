package br.com.Vestibuline.domain.simulado.dto.personalizado;

import br.com.Vestibuline.domain.simulado.validacoes.DadosEntradaSimulado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SimuladoPersonalizadoRequestDTO(

        @NotEmpty(message = "A lista de fundamentos não pode ser vazia.")
        List<String> fundamentos,

        @NotBlank(message = "A sigla não pode ser vazia.")
        String sigla,

        @NotNull(message = "A quantidade de questões é obrigatória.")
        Integer quantidade_questoes
) implements DadosEntradaSimulado {}
