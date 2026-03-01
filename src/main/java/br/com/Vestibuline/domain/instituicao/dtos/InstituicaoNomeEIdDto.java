package br.com.Vestibuline.domain.instituicao.dtos;

import java.util.UUID;

public record InstituicaoNomeEIdDto(
        UUID id,
        String slug
) {
}
