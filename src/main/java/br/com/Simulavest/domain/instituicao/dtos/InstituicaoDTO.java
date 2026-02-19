package br.com.Simulavest.domain.instituicao.dtos;

import br.com.Simulavest.domain.instituicao.Instituicao;
import br.com.Simulavest.domain.instituicao.TipoInstituicao;
import br.com.Simulavest.domain.prova.Prova;

import java.util.*;
import java.util.stream.*;

public record InstituicaoDTO(
        UUID id,
        String fullName,
        TipoInstituicao type,
        String slug,
        String logo,
        String state,
        List<Integer> year,
        Map<Integer, List<Integer>> dia,
        int totalQuestions
) {

    public InstituicaoDTO(Instituicao i) {
        this(i, gerarMapaCronograma(i.getProvas()));
    }

    private InstituicaoDTO(Instituicao i, Map<Integer, List<Integer>> cronograma) {
        this(
                i.getId(),
                i.getNome(),
                i.getTipoInstituicao(),
                i.getSigla(),
                i.getLogo(),
                i.getEstadoOrigem(),
                new ArrayList<>(cronograma.keySet()),
                cronograma,
                i.getProvas().isEmpty() ? 0 : i.getProvas().get(0).getQtdeQuestoes()
        );
    }

    // Método auxiliar para organizar as provas por Ano -> Lista de Dias
    private static Map<Integer, List<Integer>> gerarMapaCronograma(List<Prova> provas) {
        return provas.stream()
                .collect(Collectors.groupingBy(
                        Prova::getAno,
                        TreeMap::new,
                        Collectors.mapping(
                                Prova::getDia,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        list -> list.stream()
                                                .filter(Objects::nonNull)
                                                .distinct()
                                                .sorted()
                                                .toList()
                                )
                        )
                ));
    }
}