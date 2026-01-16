package br.com.Simulavest.domain.simulado.validacoes;

import br.com.Simulavest.domain.instituicao.InstituicaoRepository;
import br.com.Simulavest.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorInstituicaoPersonalizado implements ValidadorSimuladoPersonalizado {

    @Autowired
    private InstituicaoRepository repository;

    @Override
    public void validar(DadosEntradaSimulado dto) {

        if (!repository.existsBySiglaIgnoreCase(dto.sigla())) {
            throw new ResourceNotFoundException("Instituição não encontrada com a sigla: " + dto.sigla());
        }
    }
}
