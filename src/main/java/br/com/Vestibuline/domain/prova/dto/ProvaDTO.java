package br.com.Vestibuline.domain.prova.dto;

import br.com.Vestibuline.domain.prova.Prova;
import br.com.Vestibuline.domain.questao.dto.QuestaoDTO;

import java.util.Comparator;
import java.util.List;

public record ProvaDTO(
        String nomeUniversidade,
        String siglaUniversidade,
        String nomeProva,
        int ano,
        int qtdeQuestoes,
        int dia,
        List<QuestaoDTO> questoes
) {
    public ProvaDTO(Prova p) {
        this(
                p.getInstituicao().getNome(),
                p.getInstituicao().getSigla(),
                p.getNome(),
                p.getAno(),
                p.getQtdeQuestoes(),
                p.getDia(),
                p.getQuestoes().stream()
                        .map(QuestaoDTO::new).sorted(Comparator.comparingInt(QuestaoDTO::numeroEnunciado)).toList()
        );
    }
}