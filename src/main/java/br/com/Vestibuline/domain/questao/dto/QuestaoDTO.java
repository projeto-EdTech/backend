package br.com.Vestibuline.domain.questao.dto;

import br.com.Vestibuline.domain.alternativa.Alternativa;
import br.com.Vestibuline.domain.alternativa.dto.AlternativaDTO;
import br.com.Vestibuline.domain.questao.Questao;

import java.util.List;

public record QuestaoDTO(
        int numeroEnunciado,
        String enunciado,
        List<AlternativaDTO> alternativas,
        String opcaoCorreta,
        List<String> conteudo
) {
    public QuestaoDTO(Questao q) {
        this(
                q.getNumeroQuestao(),
                q.getEnunciado(),
                q.getAlternativas().stream().map(AlternativaDTO::new).toList(),
                q.getAlternativas().stream().filter(Alternativa::isCorreta).map(Alternativa::getAlternativa).findFirst().orElse(null),
                q.getConteudos() != null ? q.getConteudos().stream().map(c -> c.getMateria().getNome() + " - " + c.getNome()).toList() : List.of()
        );
    }
}