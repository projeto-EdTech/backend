package br.com.Simulavest.service;

import br.com.Simulavest.domain.conteudo.Conteudo;
import br.com.Simulavest.domain.questao.Questao;
import br.com.Simulavest.domain.questao.QuestaoRepository;
import br.com.Simulavest.domain.simulado.SimuladoPersonalizadoDTO;
import br.com.Simulavest.exception.QuestoesInsuficientesException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class SimuladoPersonalizadoService {

    private final QuestaoRepository repository;


    public SimuladoPersonalizadoService(QuestaoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public long quantidadeQuestoes(List<String> fundamentos, String silga) {
        if(fundamentos == null || fundamentos.isEmpty() || silga == null || silga.isEmpty()) {
            return 0L;
        }
        String instituicaoLowerCase = silga.toLowerCase();

        List<String> fundamentosLowerCase = fundamentos.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        return repository.quantidadeQuestoesDisponiveis(fundamentosLowerCase, instituicaoLowerCase);
    }

    @Transactional(readOnly = true)
    public List<SimuladoPersonalizadoDTO> iniciarSimulado(String sigla, List<String> fundamentos, int quantidade_questoes) {
        if (quantidade_questoes < fundamentos.size()) {
            throw new IllegalArgumentException(String.format(
                    "A quantidade de questões solicitada (%d) é menor que o número de fundamentos selecionados (%d).",
                    quantidade_questoes, fundamentos.size()));
        }

        String instituicaoLowerCase = sigla.toLowerCase();
        List<String> fundamentosLowerCase = fundamentos.stream().map(String::toLowerCase).collect(Collectors.toList());
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
            throw new QuestoesInsuficientesException(String.format(
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

    @Transactional(readOnly = true)
    public List<SimuladoPersonalizadoDTO> iniciarSimuladoMix(String sigla, int quantidadeSolicitada) {

        List<Questao> todasQuestoes = repository.buscarQuestoesPorInstituicao(sigla.toLowerCase());

        if (todasQuestoes.size() < quantidadeSolicitada) {
            throw new QuestoesInsuficientesException(String.format(
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