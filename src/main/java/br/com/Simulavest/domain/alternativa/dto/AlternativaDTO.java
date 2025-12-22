package br.com.Simulavest.domain.alternativa.dto;

import br.com.Simulavest.domain.alternativa.Alternativa;

public record AlternativaDTO(
        String letra,
        String texto
) {
    public AlternativaDTO(Alternativa a) {
        this(
                a.getAlternativa(),
                a.getTextoAlternativa()
        );
    }
}