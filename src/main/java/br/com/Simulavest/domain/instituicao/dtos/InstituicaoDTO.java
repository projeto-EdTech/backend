package br.com.Simulavest.domain.instituicao.dtos;

import br.com.Simulavest.domain.instituicao.Instituicao;
import br.com.Simulavest.domain.instituicao.TipoInstituicao;
import br.com.Simulavest.domain.prova.Prova;

import java.util.ArrayList;
import java.util.UUID;

public record InstituicaoDTO(
        UUID id,
        String fullName,
        TipoInstituicao tipoInstituicao,
        String slug,
        String logo,
        String state,
        ArrayList year,
        int totalQuestions
) {

    public InstituicaoDTO(Instituicao i) {
        this(
                i.getId(),
                i.getNome(),
                i.getTipoInstituicao(),
                i.getSigla(),
                i.getLogo(),
                i.getEstadoOrigem(),
                i.getProvas().stream()
                        .map(Prova::getAno)
                        .distinct()
                        .sorted()
                        .toList()
                        .stream()
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll),
                i.getProvas().isEmpty() ? 0 : i.getProvas().getFirst().getQtdeQuestoes()
        );
    }
}