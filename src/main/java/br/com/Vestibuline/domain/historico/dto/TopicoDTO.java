package br.com.Vestibuline.domain.historico.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TopicoDTO (
        @JsonProperty("nome")
        String nome,

        @JsonProperty("quantidadeErros")
        Long quantidadeErros
){}
