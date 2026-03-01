package br.com.Simulavest.domain.artigo.dto;

import br.com.Simulavest.domain.artigo.Artigo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.List;
import java.util.UUID;

public record ArtigoDto(
        UUID id,
        @JsonProperty("title")
        String titulo,
        @JsonProperty("content")
        String conteudoHtml,
        @JsonProperty("author")
        String autor,
        @JsonProperty("publishedAt")
        String dataPublicacao,
        @JsonProperty("category")
        String materia,
        ArtigosStatsDto stats
) {
    public ArtigoDto(Artigo artigo) {
        this(
                artigo.getId(),
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
                artigo.getTitulo(),
                html,
                artigo.getCriadoPor(),
                artigo.getCriadoEm().toString(),
                artigo.getMateria().getNome(),
                new ArtigosStatsDto(artigo.getArtigoStats())
        );
    }
}
