package br.com.Simulavest.domain.historico.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record HistoricoDTO(
        @NotNull UUID usuario_id,
        @NotNull UUID prova_id,
        String feedback_gemini,
        @NotNull Integer quantidade_acertos,
        @NotNull Integer quantidade_erros,
        @NotNull Double nota_final
) {}
