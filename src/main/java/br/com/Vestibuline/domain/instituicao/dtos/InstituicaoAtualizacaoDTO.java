package br.com.Vestibuline.domain.instituicao.dtos;

import br.com.Vestibuline.domain.instituicao.TipoInstituicao;

public record InstituicaoAtualizacaoDTO(
        String nome,
        TipoInstituicao tipoInstituicao,
        String sigla,
        String logo
) {
}
