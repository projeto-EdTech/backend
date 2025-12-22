package br.com.Simulavest.domain.prova.dto;

import br.com.Simulavest.domain.instituicao.Instituicao;
import br.com.Simulavest.domain.prova.Prova;
import br.com.Simulavest.domain.questao.Questao;
import br.com.Simulavest.domain.questao.dto.QuestaoDTO;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Comparator;
import java.util.List;

public record ProvaDTO(
        String nomeUniversidade,
        String siglaUniversidade,
        String nomeProva,
        int ano,
        int qtdeQuestoes,
        List<QuestaoDTO> questoes
) {
    public ProvaDTO(Prova p) {
        this(
                p.getInstituicao().getNome(),
                p.getInstituicao().getSigla(),
                p.getNome(),
                p.getAno(),
                p.getQtdeQuestoes(),
                p.getQuestoes().stream()
                        .map(QuestaoDTO::new).sorted(Comparator.comparingInt(QuestaoDTO::numeroEnunciado)).toList()
        );
    }
}