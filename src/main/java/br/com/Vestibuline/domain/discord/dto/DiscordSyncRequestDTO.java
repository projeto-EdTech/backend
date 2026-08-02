package br.com.Vestibuline.domain.discord.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DiscordSyncRequestDTO(
        @NotBlank(message = "O token é obrigatório.")
        @Pattern(regexp = "^VEST-[A-Z2-9]{5}$", message = "Formato de token inválido. Deve ser VEST-XXXXX")
        String token,
        @NotBlank(message = "O ID do Discord é obrigatório.")
        @Pattern(regexp = "^\\d{17,20}$", message = "ID do Discord inválido.")
        String discord_id
) {}
