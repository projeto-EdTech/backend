package br.com.Vestibuline.domain.resposta.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ItemRespostaDTO(

        @NotNull(message = "O ID da questão é obrigatório!")
        UUID id_questao,
        @NotNull(message = "O ID da alternativa escolhida é obrigatório!")
        UUID id_AlternativaEscolhida
) {
}
