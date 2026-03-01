package br.com.Vestibuline.domain.resposta.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public record CorrecaoDTO(
        int numeroQuestao,
        boolean acertou,
        String materia,
        String suaResposta,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        String respostaCorreta
) {
}
