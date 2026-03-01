package br.com.Simulavest.domain.artigo.dto;

import br.com.Simulavest.domain.artigo.ArtigoStats;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ArtigosStatsDto(
        @JsonProperty("likes")
        int likes,
        @JsonProperty("views")
        int views,
        @JsonProperty("shares")
        int shares,
        @JsonProperty("readingTime")
        String readingTime
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
