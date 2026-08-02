package br.com.Vestibuline.domain.questao.dto;

import br.com.Vestibuline.domain.alternativa.dto.AlternativaRevisaoDTO;

import java.util.List;
import java.util.UUID;

public record QuestaoRevisaoDTO(
        UUID id_questao,
        String fundamento,
        String enunciado,
        List<AlternativaRevisaoDTO> alternativas,
        List<String> imagens
) {}
