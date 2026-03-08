package br.com.Vestibuline.domain.flashcard.dto;

import br.com.Vestibuline.domain.historico.dto.MateriaDesempenhoDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record FlashCardDTO(
        @JsonProperty("materias")
        List<MateriaDesempenhoDTO> materias
) {}
