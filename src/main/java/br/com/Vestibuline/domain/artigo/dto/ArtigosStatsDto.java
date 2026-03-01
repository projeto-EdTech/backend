package br.com.Vestibuline.domain.artigo.dto;

import br.com.Vestibuline.domain.artigo.ArtigoStats;

public record ArtigosStatsDto(
        int curtidas,
        int visualizacoes,
        int compartilhamentos,
        String tempoMedioLeitura
) {
    public ArtigosStatsDto(ArtigoStats artigoStats) {
        this(
                artigoStats.getCurtidas(),
                artigoStats.getVisualizacoes(),
                artigoStats.getCompartilhamentos(),
                artigoStats.getTempoMedioLeitura()
        );
    }
}
