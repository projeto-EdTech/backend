package br.com.Simulavest.domain.instituicao.dtos;

import br.com.Simulavest.domain.instituicao.Instituicao;

import java.util.UUID;

public record InstituicaoNomeEIdDto(
        UUID id,
        String slug
) {
}
