package br.com.Vestibuline.domain.resposta.dto;

import br.com.Vestibuline.domain.simulado.TipoSimulado;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SimuladoInputDTO(

        @NotNull(message = "O tipo de simulado é obrigatório (MIX/PERSONALIZADO)")
        TipoSimulado tipo_simulado,

        @NotNull(message = "O tempo gasto para realizar o simulado é obrigatório!")
        Integer tempo_gasto,

        List<ItemRespostaDTO> respostas
) {}
