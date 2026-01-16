package br.com.Simulavest.service;

import br.com.Simulavest.domain.instituicao.InstituicaoRepository;
import br.com.Simulavest.domain.questao.Questao;
import br.com.Simulavest.domain.questao.QuestaoRepository;
import br.com.Simulavest.domain.questao.validacoes.ValidadorSimuladoMix;
import br.com.Simulavest.domain.simulado.dto.mix.SimuladoMixRequestDTO;
import br.com.Simulavest.domain.simulado.dto.personalizado.SimuladoPersonalizadoDTO;
import br.com.Simulavest.exception.ResourceNotFoundException; // Não esqueça deste import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SimuladoMixService {

    @Autowired
    private QuestaoRepository questaoRepository;
    @Autowired
    private InstituicaoRepository instituicaoRepository;
    @Autowired
    private List<ValidadorSimuladoMix> validador;


    @Transactional(readOnly = true)
    public List<SimuladoPersonalizadoDTO> iniciarSimuladoMix(SimuladoMixRequestDTO dto) {

        var sigla = dto.sigla();
        var quantidadeSolicitada = dto.quantidade_questoes();

        validador.forEach(v -> v.validar(dto));

        List<Questao> todasQuestoes = questaoRepository.buscarQuestoesPorInstituicao(sigla.toLowerCase());

        if (todasQuestoes.size() < quantidadeSolicitada) {
            throw new ResourceNotFoundException(String.format(
                    "Quantidade insuficiente. A instituição %s possui apenas %d questões cadastradas.",
                    sigla.toUpperCase(), todasQuestoes.size()
            ));
        }

        Collections.shuffle(todasQuestoes);

        List<Questao> questoesSelecionadas = todasQuestoes.subList(0, quantidadeSolicitada);

        return questoesSelecionadas.stream()
                .map(SimuladoPersonalizadoDTO::new)
                .collect(Collectors.toList());
    }
}