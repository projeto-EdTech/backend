package br.com.Simulavest.domain.nota_corte.dto;

public record NotaCorteResponseDTO(
        String curso,
        Double mediaNotaCorte,
        Integer totalProvas,
        String instituicao
) {}
