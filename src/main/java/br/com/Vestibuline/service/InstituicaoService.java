package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.alternativa.Alternativa;
import br.com.Vestibuline.domain.instituicao.Instituicao;
import br.com.Vestibuline.domain.instituicao.dtos.*;
import br.com.Vestibuline.domain.instituicao.InstituicaoRepository;
import br.com.Vestibuline.domain.instituicao.dtos.EstatisticaMateriaDto;
import br.com.Vestibuline.domain.materia.MateriaRepository;
import br.com.Vestibuline.domain.prova.Prova;
import br.com.Vestibuline.domain.prova.dto.ProvaDTO;
import br.com.Vestibuline.domain.questao.Questao;
import br.com.Vestibuline.domain.questao.QuestaoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Service
public class InstituicaoService {

    @Autowired
    private InstituicaoRepository repository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired QuestaoRepository questaoRepository;

    @Autowired
    private MateriaService materiaService;

    @Autowired
    private ConteudoService conteudoService;

    @Transactional
    public Instituicao cadastrarInstituicao(InstituicaoRequestDTO dto) {
        Instituicao instituicao = new Instituicao(dto);

        return repository.save(instituicao);
    }

    public List<InstituicaoDTO> listarInstituicoes() {
        var listaInstituicoes = repository.findAll();
        return listaInstituicoes.stream()
                .map(InstituicaoDTO::new).toList();
    }

    @Transactional
    public Instituicao atualizarInstituicao(UUID id , InstituicaoAtualizacaoDTO dto) {
        var instituicaoExistente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Instituição não encontrada com o ID: " + id));

        instituicaoExistente.atualizarDados(dto);

        return repository.save(instituicaoExistente);
    }

    @Transactional
    public void adicionarProva(@Valid ProvaDTO dto) {
        try{
            var instituicao = repository.findBySigla(dto.siglaUniversidade()).get();

            var prova = new Prova(dto);
            instituicao.adicionarProva(prova);

            for (int i = 0; i < dto.questoes().size() ; i++) {
                var questao = new Questao(dto.questoes().get(i));
                prova.adicionarQuestao(questao);
                for (int j = 0; j < dto.questoes().get(i).conteudo().size() ; j++) {
                    var materia = materiaService.verificarMateria(dto.questoes().get(i).conteudo().get(j).split("\\s+[-–—]\\s+")[0].trim());
                    var conteudo = dto.questoes().get(i).conteudo().get(j).split("\\s+[-–—]\\s+")[1].trim();
                    var conteudoEntidade = conteudoService.verificarConteudo(conteudo, materia);
                    questao.adicionarConteudos(conteudoEntidade, materia);
                }

                for (var alternativaDto : dto.questoes().get(i).alternativas()) {
                    var alternativa = new Alternativa(alternativaDto, dto.questoes().get(i).opcaoCorreta());
                    questao.adicionarAlternativa(alternativa);
                }
            }

            repository.save(instituicao);
        }catch (Exception e) {
            throw new IllegalArgumentException("Erro ao adicionar prova: " + e.getMessage());
        }
    }

    public InstituicaoCompletaDto buscarInstituicaoPorId(UUID id) {
        var instituicao = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Instituição não encontrada com o ID: " + id));

        var provasDto = instituicao.getProvas().stream()
                .map(ProvaDTO::new)
                .toList();

        return new InstituicaoCompletaDto(
                instituicao.getId(),
                instituicao.getNome(),
                instituicao.getSigla(),
                instituicao.getTipoInstituicao(),
                instituicao.getLogo(),
                provasDto
        );
    }

    public List<EstatisticaMateriaDto> obterEstatisticasPorUniversidadeEMateria(String universidadeId, String nome_materia) {
        if (nome_materia.isBlank() || universidadeId == null || universidadeId.isBlank()) {
            throw new IllegalArgumentException("ID da matéria ou da universidade inválido.");
        }

        // Busca a matéria
        var materiaEscolhida = materiaRepository.findByNome(nome_materia)
                .orElseThrow(() -> new IllegalArgumentException("Matéria não encontrada com o ID: " + nome_materia));

        // Define as questões conforme o tipo de busca
        List<Questao> questoes;
        if (universidadeId.equals("all")) {
            questoes = questaoRepository.findQuestoesByMateriaId(materiaEscolhida.getId());
        } else {
            questoes = questaoRepository.findQuestoesByMateriaAndInstituicaoId(materiaEscolhida.getId(), UUID.fromString(universidadeId));
        }

        int totalQuestoes = questoes.size();
        System.out.println("Total de questões na matéria " + materiaEscolhida.getNome() + ": " + totalQuestoes);

        // Cálculo base (pesos brutos)
        var conteudos = materiaEscolhida.getConteudos();

        List<Double> pesos = conteudos.stream()
                .map(conteudo -> questoes.stream()
                        .filter(q -> q.getConteudos().contains(conteudo))
                        .mapToDouble(q -> 1.0 / q.getConteudos().size())
                        .sum()
                ).toList();

        // Soma total dos pesos
        double somaPesos = pesos.stream().mapToDouble(Double::doubleValue).sum();

        //lista de top 10 conteudos que mais aparecem nas questões + outros(soma do restante fora do top 10)
        List<EstatisticaMateriaDto> estatisticas = IntStream.range(0, conteudos.size())
                .mapToObj(i -> {
                    double pesoNormalizado = (somaPesos == 0) ? 0 : (pesos.get(i) / somaPesos) * 100;
                    BigDecimal pesoArredondado = BigDecimal.valueOf(pesoNormalizado).setScale(2, RoundingMode.HALF_UP);
                    return new EstatisticaMateriaDto(
                            conteudos.get(i).getNome(),
                            pesoArredondado.doubleValue()
                    );
                })
                .filter(e -> e.percentual() > 0)
                .sorted(Comparator.comparing(EstatisticaMateriaDto::percentual).reversed())
                .collect(toList());

        if (estatisticas.size() > 10) {
            List<EstatisticaMateriaDto> top10 = estatisticas.subList(0, 10);
            double outrosPercentual = estatisticas.subList(10, estatisticas.size()).stream()
                    .mapToDouble(EstatisticaMateriaDto::percentual)
                    .sum();
            top10.add(new EstatisticaMateriaDto("Outros", BigDecimal.valueOf(outrosPercentual).setScale(2, RoundingMode.HALF_UP).doubleValue()));
            return top10;
        } else {
            return estatisticas;
        }
    }


}