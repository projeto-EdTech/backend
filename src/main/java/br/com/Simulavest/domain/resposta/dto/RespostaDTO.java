package br.com.Simulavest.domain.resposta.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RespostaDTO(

        @NotNull(message = "O ID da questão é obrigatório!")
        UUID id_questao,

        @NotNull(message = "O ID da alternativa é obrigatório!")
        UUID id_alternativa_escolhida
) {}
