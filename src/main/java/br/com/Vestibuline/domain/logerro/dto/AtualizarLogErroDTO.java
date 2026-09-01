package br.com.Vestibuline.domain.logerro.dto;

import br.com.Vestibuline.domain.logerro.StatusIncidente;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AtualizarLogErroDTO(
        @NotNull(message = "O status é obrigatório.")
        StatusIncidente status,

        @Size(max = 4000, message = "Observações devem ter no máximo 4000 caracteres.")
        String observacoes,

        @Size(max = 50, message = "Versão deve ter no máximo 50 caracteres.")
        String versaoCorrigida
) {}
