package br.com.Simulavest.domain.artigo.dto;

import br.com.Simulavest.domain.artigo.Artigo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.List;
import java.util.UUID;

public record ArtigoDto(
        UUID id,
        String slug,
        @JsonProperty("title")
        String title,
        @JsonProperty("content")
        String content,
        @JsonProperty("author")
        String author,
        @JsonProperty("publishedAt")
        String publishedAt,
        @JsonProperty("category")
        String category,
        ArtigosStatsDto stats
) {
    public ArtigoDto(Artigo artigo) {
        this(
                artigo.getId(),
                artigo.getTitulo().trim().replace(" ", "-"),
                artigo.getTitulo(),
                artigo.getConteudo(),
                artigo.getCriadoPor(),
                artigo.getCriadoEm().toString(),
                artigo.getMateria().getNome(),
                new ArtigosStatsDto(artigo.getArtigoStats())
        );
    }

    public ArtigoDto(Artigo artigo, String html) {
        this(
                artigo.getId(),
                artigo.getTitulo().trim().replace(" ", "-"),
                artigo.getTitulo(),
                html,
                artigo.getCriadoPor(),
                artigo.getCriadoEm().toString(),
                artigo.getMateria().getNome(),
                new ArtigosStatsDto(artigo.getArtigoStats())
        );
    }
}
