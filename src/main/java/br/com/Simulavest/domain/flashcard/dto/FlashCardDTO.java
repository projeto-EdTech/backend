package br.com.Simulavest.domain.flashcard.dto;

import br.com.Simulavest.domain.historico.dto.MateriaDesempenhoDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record FlashCardDTO(
        @JsonProperty("materias")
        List<MateriaDesempenhoDTO> materias
) {}
