package br.com.Vestibuline.domain.planner.dto;

import java.util.List;

public record MateriaPlanoDTO(
        String materiaId,
        String materiaNome,
        double taxaErroMateria,
        int cardsAlocados,
        List<ConteudoPlanoDTO> conteudos
) {}