package br.com.Simulavest.domain.instituicao.dtos;

import br.com.Simulavest.domain.instituicao.TipoInstituicao;

public record InstituicaoAtualizacaoDTO(
        String nome,
        TipoInstituicao tipoInstituicao,
        String sigla,
        String logo
) {
}
