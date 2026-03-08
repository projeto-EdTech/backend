package br.com.Vestibuline.domain.nota_corte.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record CursoItemDTO(

        @JsonAlias("nomeCurso")
        String nome,

        @JsonAlias("modalidadeConcorrencia")
        String modalidade,

        @JsonAlias("notaCorte")
        Double nota
) {}
