package br.com.Vestibuline.domain.gabarito.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record GabaritoRequestDTO(
        @NotNull UUID usuarioId,
        @NotNull UUID questaoId,
        @NotNull Character respostaUsuario
) {
}
