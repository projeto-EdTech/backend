package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.alternativa.Alternativa;
import br.com.Vestibuline.domain.alternativa.AlternativaRepository;
import br.com.Vestibuline.domain.historico.Historico;
import br.com.Vestibuline.domain.historico.HistoricoRepository;
import br.com.Vestibuline.domain.resposta.dto.CorrecaoDTO;
import br.com.Vestibuline.domain.historico.dto.HistoricoDTO;
import br.com.Vestibuline.domain.resposta.dto.ResumoDTO;
import br.com.Vestibuline.domain.questao.Questao;
import br.com.Vestibuline.domain.questao.QuestaoRepository;
import br.com.Vestibuline.domain.resposta.Resposta;
import br.com.Vestibuline.domain.resposta.RespostaRepository;
import br.com.Vestibuline.domain.resposta.dto.ItemRespostaDTO;
import br.com.Vestibuline.domain.resposta.dto.SimuladoInputDTO;
import br.com.Vestibuline.domain.usuario.UsuarioRepository;
import br.com.Vestibuline.exception.RegraDeNegocioException;
import br.com.Vestibuline.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class HistoricoService {

    @Autowired private HistoricoRepository historicoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private QuestaoRepository questaoRepository;
    @Autowired private AlternativaRepository alternativaRepository;
    @Autowired private RespostaRepository respostaRepository;

    @Transactional
    public HistoricoDTO cadastrarHistorico(UUID usuarioId, SimuladoInputDTO dto) {

        Historico historico = new Historico();
        historico.setUsuario(usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado")));
        historico.setTipo_simulado(dto.tipo_simulado());
        historico.setData(LocalDate.now());
        historico.setTempoGasto(dto.tempo_gasto());

        historicoRepository.save(historico);

        List<Resposta> respostasParaSalvar = new ArrayList<>();
        List<CorrecaoDTO> listaCorrecao = new ArrayList<>();
        int acertos = 0;
        int totalQuestoesRespondidas = dto.respostas().size();


        for (ItemRespostaDTO item : dto.respostas()) {

            Questao questao = questaoRepository.findById(item.id_questao())
                    .orElseThrow(() -> new ResourceNotFoundException("Questão não encontrada"));

            Alternativa alternativa = alternativaRepository.findById(item.id_AlternativaEscolhida())
                    .orElseThrow(() -> new ResourceNotFoundException("Alternativa não encontrada"));

            if (!alternativa.getQuestao().getId().equals(questao.getId())) {
                throw new RegraDeNegocioException(
                        "Inconsistência detectada: A alternativa " + alternativa.getId() +
                                " não pertence à questão " + questao.getId()
                );
            }

            boolean acertou = alternativa.isCorreta();
            if (acertou) acertos++;

            Resposta resposta = new Resposta();
            resposta.setHistorico(historico);
            resposta.setQuestao(questao);
            resposta.setAlternativaEscolhida(alternativa);
            resposta.setAcertou(acertou);
            respostasParaSalvar.add(resposta);

            String letraCorreta = null;
            if (!acertou) {
                letraCorreta = questao.getAlternativas().stream()
                        .filter(Alternativa::isCorreta)
                        .findFirst()
                        .map(Alternativa::getAlternativa)
                        .orElse("?");
            }

            listaCorrecao.add(new CorrecaoDTO(
                    questao.getNumeroQuestao(),
                    acertou,
                    questao.getConteudos().isEmpty() ? "Geral" : questao.getConteudos().get(0).getMateria().getNome(),
                    alternativa.getAlternativa(),
                    letraCorreta,
                    questao.getImagens()
            ));
        }

        historico.setQuantidade_acertos(acertos);
        historico.setQuantidade_questoes(totalQuestoesRespondidas);

        respostaRepository.saveAll(respostasParaSalvar);

        int total = dto.respostas().size();
        double porcentagem = total > 0 ? (double) acertos / total * 100 : 0.0;

        ResumoDTO resumo = new ResumoDTO(
                total,
                acertos,
                total - acertos,
                String.format("%.1f%%", porcentagem)
        );

        return new HistoricoDTO(
                historico.getId(),
                historico.getData(), // "2026-02-07T20:00:00"
                resumo,
                listaCorrecao
        );
    }
}
