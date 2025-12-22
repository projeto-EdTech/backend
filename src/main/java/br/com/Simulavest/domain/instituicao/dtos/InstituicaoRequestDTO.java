package br.com.Simulavest.domain.instituicao.dtos;

import br.com.Simulavest.domain.instituicao.TipoInstituicao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InstituicaoRequestDTO(
        @NotNull(message = "O nome é obrigatório")
        @NotBlank(message = "O nome não pode estar em branco")
        String nome,

        @NotNull(message = "O tipo de instituição é obrigatório")
        TipoInstituicao tipoInstituicao,

        @NotNull(message = "A sigla é obrigatória")
        @NotBlank(message = "A sigla não pode estar em branco")
        String sigla,

        String logo,

        @NotNull(message = "O estado de origem é obrigatório")
        @NotBlank(message = "O estado de origem não pode estar em branco")
        String estadoOrigem
) { }
