package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.usuario.Rank;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

@Converter(autoApply = true)
public class RankConverter implements AttributeConverter<Rank, String> {

    @Override
    public String convertToDatabaseColumn(Rank rank) {
        return rank == null ? null : rank.getLabel();
    }

    @Override
    public Rank convertToEntityAttribute(String value) {
        if (value == null) return null;
        return Arrays.stream(Rank.values())
                .filter(r -> r.getLabel().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Rank inválido: " + value));
    }
}
