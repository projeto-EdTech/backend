package br.com.Simulavest.domain.simulado.dto.finalizado;

import java.util.UUID;

public record SimuladoResultadoDTO(

        UUID idHistorico,
        Integer totalQuestoes,
        Integer totalAcertos,
        Integer totalErros,
        String aproveitamento
) {}
