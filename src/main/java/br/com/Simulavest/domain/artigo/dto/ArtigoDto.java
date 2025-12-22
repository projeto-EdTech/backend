package br.com.Simulavest.domain.artigo.dto;

import br.com.Simulavest.domain.artigo.Artigo;

import java.util.List;
import java.util.UUID;

public record ArtigoDto(
        UUID id,
        String titulo,
        String conteudoHtml,
        String autor,
        String dataPublicacao,
        ArtigosStatsDto stats
) {
    public ArtigoDto(Artigo artigo) {
        this(
                artigo.getId(),
                artigo.getTitulo(),
                artigo.getConteudo(),
                artigo.getCriadoPor(),
                artigo.getCriadoEm().toString(),
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
                new ArtigosStatsDto(artigo.getArtigoStats())
        );
    }
}
