package br.com.Vestibuline.domain.instituicao.dtos;

import br.com.Vestibuline.domain.instituicao.TipoInstituicao;
import br.com.Vestibuline.domain.prova.dto.ProvaDTO;

import java.util.List;
import java.util.UUID;

public record InstituicaoCompletaDto(
        UUID id,
        String nome,
        String sigla,
        TipoInstituicao tipoInstituicao,
        String logo,
        List<ProvaDTO> provas
) {
}
