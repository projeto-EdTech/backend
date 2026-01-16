package br.com.Simulavest.domain.questao.dto;

import br.com.Simulavest.domain.simulado.validacoes.DadosEntradaSimulado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ContagemQuestaoRequestDTO(
        @NotEmpty(message = "A lista de fundamentos não pode ser vazia!")
        List<String> fundamentos,

        @NotBlank(message = "A sigla não pode ser vazia!")
        String sigla
) implements DadosEntradaSimulado {}