package br.com.Simulavest.domain.historico.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MateriaDesempenhoDTO(

        @JsonProperty("nome")
        String nomeMateria,

        @JsonProperty("topicos")
        List<TopicoDTO> topicos
) {
}
