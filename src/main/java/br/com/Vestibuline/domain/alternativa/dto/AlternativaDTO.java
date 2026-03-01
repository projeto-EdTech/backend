package br.com.Vestibuline.domain.alternativa.dto;

import br.com.Vestibuline.domain.alternativa.Alternativa;

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