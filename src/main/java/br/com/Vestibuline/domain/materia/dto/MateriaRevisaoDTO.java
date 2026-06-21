package br.com.Vestibuline.domain.materia.dto;

import br.com.Vestibuline.domain.questao.dto.QuestaoRevisaoDTO;

import java.util.List;

public record MateriaRevisaoDTO(
        String materia,
        List<QuestaoRevisaoDTO> questoes
) {}
