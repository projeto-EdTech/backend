package br.com.Vestibuline.domain.resposta.dto;

public record ResumoDTO(
        int totalQuestoes,
        int acertos,
        int erros,
        String aproveitamento
) {
}
