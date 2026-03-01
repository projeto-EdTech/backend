package br.com.Simulavest.domain.artigo.dto;

import br.com.Simulavest.domain.artigo.ArtigoStats;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ArtigosStatsDto(
        @JsonProperty("likes")
        int curtidas,
        @JsonProperty("views")
        int visualizacoes,
        @JsonProperty("shares")
        int compartilhamentos,
        @JsonProperty("readingTime")
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
