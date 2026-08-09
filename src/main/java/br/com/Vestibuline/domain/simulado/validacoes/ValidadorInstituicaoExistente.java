package br.com.Vestibuline.domain.simulado.validacoes;

import br.com.Vestibuline.domain.instituicao.InstituicaoRepository;
import br.com.Vestibuline.domain.questao.validacoes.ValidadorSimuladoMix;
import br.com.Vestibuline.domain.simulado.dto.mix.SimuladoMixRequestDTO;
import br.com.Vestibuline.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// Consolida a checagem "instituição existe pela sigla", antes duplicada entre
// ValidadorInstituicaoMix e ValidadorInstituicaoPersonalizado.
@Component
public class ValidadorInstituicaoExistente implements ValidadorSimuladoMix, ValidadorSimuladoPersonalizado {

    @Autowired
    private InstituicaoRepository repository;

    @Override
    public void validar(SimuladoMixRequestDTO dto) {
        validarSigla(dto);
    }

    @Override
    public void validar(DadosEntradaSimulado dto) {
        validarSigla(dto);
    }

    private void validarSigla(ValidavelPorSigla dto) {
        if (!repository.existsBySiglaIgnoreCase(dto.sigla())) {
            throw new ResourceNotFoundException("Instituição não encontrada com a sigla: " + dto.sigla());
        }
    }
}
