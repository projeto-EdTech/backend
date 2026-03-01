package br.com.Vestibuline.domain.simulado.validacoes;

import br.com.Vestibuline.domain.conteudo.ConteudoRepository;
import br.com.Vestibuline.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorFundamentosPersonalizado implements ValidadorSimuladoPersonalizado {

    @Autowired
    private ConteudoRepository repository;

    @Override
    public void validar (DadosEntradaSimulado dto) {
            for (String fundamento: dto.fundamentos()) {
                if (!repository.existsByNomeIgnoreCase(fundamento)) {
                    throw new ResourceNotFoundException("Conteúdo não encontrado: " + fundamento);
                }
            }

    }
}
