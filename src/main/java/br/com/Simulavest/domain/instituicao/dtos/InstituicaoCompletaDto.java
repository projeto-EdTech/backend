package br.com.Simulavest.domain.instituicao.dtos;

import br.com.Simulavest.domain.instituicao.TipoInstituicao;
import br.com.Simulavest.domain.prova.Prova;
import br.com.Simulavest.domain.prova.dto.ProvaDTO;

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
