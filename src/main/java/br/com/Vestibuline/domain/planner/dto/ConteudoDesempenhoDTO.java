package br.com.Vestibuline.domain.planner.dto;

public record ConteudoDesempenhoDTO(
        String conteudoId,
        String conteudoNome,
        long totalRespostas,
        long totalErros,
        double taxaErro
) {}