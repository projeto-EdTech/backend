package br.com.Vestibuline.domain.planner.dto;

import java.util.List;

public record MateriaDesempenhoDTO(
        String materiaId,
        String materiaNome,
        long totalRespostas,
        long totalErros,
        double taxaErro,
        List<ConteudoDesempenhoDTO> pioresConteudos
) {}