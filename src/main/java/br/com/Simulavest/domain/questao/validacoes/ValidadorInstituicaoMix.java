package br.com.Simulavest.domain.questao.validacoes;

import br.com.Simulavest.domain.instituicao.InstituicaoRepository;
import br.com.Simulavest.domain.simulado.dto.mix.SimuladoMixRequestDTO;
import br.com.Simulavest.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorInstituicaoMix implements ValidadorSimuladoMix {

    @Autowired
    private InstituicaoRepository repository;

    @Override
    public void validar (SimuladoMixRequestDTO dto) {

        if (!repository.existsBySiglaIgnoreCase(dto.sigla())) {
            throw new ResourceNotFoundException("Instituição não encontrada com a sigla: " + dto.sigla());
        }
    }
}
