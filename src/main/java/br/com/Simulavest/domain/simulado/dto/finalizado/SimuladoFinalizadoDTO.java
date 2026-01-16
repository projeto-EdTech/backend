package br.com.Simulavest.domain.simulado.dto.finalizado;

import br.com.Simulavest.domain.resposta.dto.RespostaDTO;
import br.com.Simulavest.domain.simulado.TipoSimulado;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record SimuladoFinalizadoDTO(

        @NotNull(message = "O ID do usuário é obrigatório")
        UUID id_usuario,

        @NotNull(message = "O tipo de simulado é obrigatório (MIX/PERSONALIZADO)")
        TipoSimulado tipo_simulado,

        List<RespostaDTO> respostas
) {}
