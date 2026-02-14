package br.com.Simulavest.domain.nota_corte.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record NotaCorteInputDTO(

        @JsonAlias("siglaUniversidade")
        String siglaInstituicao,

        @NotNull(message = "O ano da nota corte deve estar preenchido")
        Integer ano,

        @NotEmpty(message = "A lista de cursos deve estar preenchida")
        List<CursoItemDTO> cursos
) {}
