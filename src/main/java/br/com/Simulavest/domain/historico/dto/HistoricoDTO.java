package br.com.Simulavest.domain.historico.dto;

import br.com.Simulavest.domain.resposta.dto.CorrecaoDTO;
import br.com.Simulavest.domain.resposta.dto.ResumoDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record HistoricoDTO(

        UUID idHistorico,
        LocalDate dataRealizacao,
        ResumoDTO resumo,
        List<CorrecaoDTO> correcao
) {}
