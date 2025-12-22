package br.com.Simulavest.domain.questao.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ContagemQuestaoRequestDTO(
        @NotEmpty(message = "A lista de fundamentos não pode ser vazia!")
        List<String> fundamentos,

        @NotEmpty(message = "A sigla não pode ser vazia!")
        String sigla
) {}