package br.com.Vestibuline.domain.materia.dto;

import br.com.Vestibuline.domain.materia.Materia;

import java.util.UUID;

public record MateriaDto(
        UUID id,
        String nome
) {
    public MateriaDto(Materia m) {
        this(
                m.getId(),
                m.getNome()
        );
    }
}
