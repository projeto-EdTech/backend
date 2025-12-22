package br.com.Simulavest.service;

import br.com.Simulavest.domain.instituicao.InstituicaoRepository;
import br.com.Simulavest.domain.prova.ProvaRepository;
import br.com.Simulavest.domain.prova.dto.EscolhaProvaEAnoRequestDTO;
import br.com.Simulavest.domain.prova.dto.ProvaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProvaService {

    @Autowired
    private ProvaRepository repository;

    @Autowired
    private InstituicaoRepository instituicaoRepository;

    public ProvaDTO escolherProvaPorInstituicaoEAno(EscolhaProvaEAnoRequestDTO dto) {
        var instituicao = instituicaoRepository.findBySigla(dto.instituicao());

        if (instituicao.isEmpty()) {
            throw new IllegalArgumentException("Instituição não encontrada: " + dto.instituicao());
        }

        var prova = repository.findProvaByInstituicaoAndAno(instituicao.get(), dto.ano());

        if (prova.isEmpty()) {
            throw new IllegalArgumentException("Prova não encontrada para a instituição " + dto.instituicao() + " no ano " + dto.ano());
        }

        return new ProvaDTO(prova.get());
    }
}
