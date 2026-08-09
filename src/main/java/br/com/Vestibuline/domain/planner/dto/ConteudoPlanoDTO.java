package br.com.Vestibuline.domain.planner.dto;

public record ConteudoPlanoDTO(
        String conteudoNome,
        double taxaErroUsuario,
        double percentualNaProva,
        double pesoPrioridade,
        int cardsAlocados
) {}