package br.com.Simulavest.domain.resposta.dto;

import br.com.Simulavest.domain.simulado.TipoSimulado;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record SimuladoInputDTO(

        @NotNull(message = "O ID do usuário é obrigatório!")
        UUID id_usuario,

        @NotNull(message = "O tipo de simulado é obrigatório (MIX/PERSONALIZADO)")
        TipoSimulado tipo_simulado,

        @NotNull(message = "O tempo gasto para realizar o simulado é obrigatório!")
        Integer tempo_gasto,

        List<ItemRespostaDTO> respostas
) {}
