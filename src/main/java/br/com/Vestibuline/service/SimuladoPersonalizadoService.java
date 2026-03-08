package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.conteudo.Conteudo;
import br.com.Vestibuline.domain.conteudo.ConteudoRepository;
import br.com.Vestibuline.domain.instituicao.InstituicaoRepository;
import br.com.Vestibuline.domain.questao.Questao;
import br.com.Vestibuline.domain.questao.QuestaoRepository;
import br.com.Vestibuline.domain.questao.dto.ContagemQuestaoRequestDTO;
import br.com.Vestibuline.domain.simulado.dto.personalizado.SimuladoPersonalizadoDTO;
import br.com.Vestibuline.domain.simulado.dto.personalizado.SimuladoPersonalizadoRequestDTO;
import br.com.Vestibuline.domain.simulado.validacoes.ValidadorSimuladoPersonalizado;
import br.com.Vestibuline.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SimuladoPersonalizadoService {

    @Autowired
    private QuestaoRepository repository;
    @Autowired
    private InstituicaoRepository instituicaoRepository;
    @Autowired
    private ConteudoRepository conteudoRepository;
    @Autowired
    private List<ValidadorSimuladoPersonalizado> validadorSimuladoPersonalizados;

    @Transactional(readOnly = true)
    public long quantidadeQuestoes(ContagemQuestaoRequestDTO dto) {

        var sigla = dto.sigla();
        var fundamentos = dto.fundamentos();

        validadorSimuladoPersonalizados.forEach(v -> v.validar(dto));

        String instituicaoLowerCase = sigla.toLowerCase();
        List<String> fundamentosLowerCase = fundamentos.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        return repository.quantidadeQuestoesDisponiveis(fundamentosLowerCase, instituicaoLowerCase);
    }

    @Transactional(readOnly = true)
    public List<SimuladoPersonalizadoDTO> iniciarSimulado(SimuladoPersonalizadoRequestDTO dto) {

        var quantidade_questoes = dto.quantidade_questoes();
        var fundamentos = dto.fundamentos();
        var sigla = dto.sigla();

        validadorSimuladoPersonalizados.forEach(v -> v.validar(dto));

        if (quantidade_questoes < fundamentos.size()) {
            throw new IllegalArgumentException(String.format(
                    "A quantidade de questões solicitada (%d) é menor que o número de fundamentos selecionados (%d).",
                    quantidade_questoes, fundamentos.size()));
        }

        String instituicaoLowerCase = sigla.toLowerCase();
        List<String> fundamentosLowerCase = fundamentos.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        List<Questao> questoesDisponiveis = repository.buscarQuestoesPorInstituicaoEFundamentos(instituicaoLowerCase, fundamentosLowerCase);

        Map<String, List<Questao>> questoesPorFundamento = new HashMap<>();
        for (Questao questao : questoesDisponiveis) {
            for (Conteudo conteudo : questao.getConteudos()) {
                String fundamentoNomeLower = conteudo.getNome().toLowerCase();
                if (fundamentosLowerCase.contains(fundamentoNomeLower)) {
                    questoesPorFundamento.computeIfAbsent(fundamentoNomeLower, k -> new ArrayList<>()).add(questao);
                }
            }
        }

        Map<String, Integer> metaPorFundamento = new HashMap<>();
        int basePorFundamento = quantidade_questoes / fundamentos.size();
        int restantes = quantidade_questoes % fundamentos.size();

        for (String fundamento : fundamentosLowerCase) {
            int meta = basePorFundamento;
            if (restantes > 0) {
                meta++;
                restantes--;
            }
            metaPorFundamento.put(fundamento, meta);
        }

        Set<Questao> questoesSelecionadas = new HashSet<>();
        List<Questao> poolGeral = new ArrayList<>();

        for (String fundamento : fundamentosLowerCase) {
            List<Questao> disponiveisDoFundamento = questoesPorFundamento.getOrDefault(fundamento, new ArrayList<>());
            Collections.shuffle(disponiveisDoFundamento);

            int meta = metaPorFundamento.get(fundamento);
            int numeroRealAPegar = Math.min(meta, disponiveisDoFundamento.size());

            int adicionadas = 0;
            for (Questao q : disponiveisDoFundamento) {
                if (adicionadas < numeroRealAPegar && !questoesSelecionadas.contains(q)) {
                    questoesSelecionadas.add(q);
                    adicionadas++;
                } else {
                    poolGeral.add(q);
                }
            }
        }

        int restantesParaPegar = quantidade_questoes - questoesSelecionadas.size();
        if (restantesParaPegar > 0) {
            poolGeral.removeAll(questoesSelecionadas);
            List<Questao> poolUnico = poolGeral.stream().distinct().collect(Collectors.toList());
            Collections.shuffle(poolUnico);

            int numeroParaPegarDoPool = Math.min(restantesParaPegar, poolUnico.size());
            questoesSelecionadas.addAll(poolUnico.subList(0, numeroParaPegarDoPool));
        }

        if (questoesSelecionadas.size() < quantidade_questoes) {
            throw new ResourceNotFoundException(String.format(
                    "Não foi possível encontrar a quantidade de questões solicitada. Solicitado: %d, Disponível: %d",
                    quantidade_questoes, questoesSelecionadas.size()
            ));
        }

        List<Questao> listaFinal = new ArrayList<>(questoesSelecionadas);
        Collections.shuffle(listaFinal);

        return listaFinal.stream()
                .map(SimuladoPersonalizadoDTO::new)
                .collect(Collectors.toList());
    }
}