package br.com.Vestibuline.domain.questao.dto;

import java.util.UUID;

public record QuestaoRevisaoRecord(
        String nomeMateria,
        String nomeFundamento,
        UUID respostaId,
        UUID questaoId,
        String enunciado,
        UUID alternativaId,
        String textoAlternativa,
        Boolean correta,
        UUID alternativaEscolhidaId
) {}
