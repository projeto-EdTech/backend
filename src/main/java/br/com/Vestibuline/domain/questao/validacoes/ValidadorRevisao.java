package br.com.Vestibuline.domain.questao.validacoes;

import br.com.Vestibuline.domain.resposta.RespostaRepository;
import br.com.Vestibuline.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ValidadorRevisao {

    private final RespostaRepository respostaRepository;

    public void validarExistenciaDeRevisaoPendente(UUID usuarioId, UUID questaoId) {
        boolean existeRevisaoPendente = respostaRepository.existsByHistoricoUsuarioIdAndQuestaoId(usuarioId, questaoId);

        if (!existeRevisaoPendente) {
            throw new ResourceNotFoundException(
                    "Nenhum registro de revisão pendente foi encontrado para a combinação de usuário e questão informada."
            );
        }
    }
}
