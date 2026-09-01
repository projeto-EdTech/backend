package br.com.Vestibuline.domain.alternativa.dto;

import java.util.UUID;

public record AlternativaRevisaoDTO(
        UUID id_alternativa,
        String texto,
        boolean correta,
        boolean escolhida
) {}
