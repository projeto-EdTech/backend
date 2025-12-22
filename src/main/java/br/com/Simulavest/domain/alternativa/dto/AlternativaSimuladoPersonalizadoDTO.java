package br.com.Simulavest.domain.alternativa.dto;

import br.com.Simulavest.domain.alternativa.Alternativa;

import java.util.UUID;

public record AlternativaSimuladoPersonalizadoDTO(
        UUID id,
        String letra,
        String texto_alternativa
) {
    public AlternativaSimuladoPersonalizadoDTO(Alternativa alternativa) {
        this(alternativa.getId(), alternativa.getAlternativa(), alternativa.getTextoAlternativa());
    }
}
