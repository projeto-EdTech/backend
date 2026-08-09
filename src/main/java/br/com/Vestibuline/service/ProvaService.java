package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.instituicao.InstituicaoRepository;
import br.com.Vestibuline.domain.prova.ProvaRepository;
import br.com.Vestibuline.domain.prova.dto.EscolhaProvaEAnoRequestDTO;
import br.com.Vestibuline.domain.prova.dto.ProvaDTO;
import br.com.Vestibuline.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProvaService {

    @Autowired
    private ProvaRepository repository;

    @Autowired
    private InstituicaoRepository instituicaoRepository;

    @Transactional(readOnly = true)
    public ProvaDTO escolherProvaPorInstituicaoEAno(EscolhaProvaEAnoRequestDTO dto) {
        var instituicao = instituicaoRepository.findBySigla(dto.instituicao())
                .orElseThrow(() -> new ResourceNotFoundException("Instituição não encontrada: " + dto.instituicao()));

        var prova = repository.findProvaByInstituicaoAndAnoAndDia(instituicao, dto.ano(), dto.dia())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Prova não encontrada para a instituição " + dto.instituicao() + " no ano " + dto.ano()));

        return new ProvaDTO(prova);
    }
}
