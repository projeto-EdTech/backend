package br.com.Simulavest.service;

import br.com.Simulavest.domain.alternativa.Alternativa;
import br.com.Simulavest.domain.alternativa.AlternativaRepository;
import br.com.Simulavest.domain.historico.Historico;
import br.com.Simulavest.domain.historico.HistoricoRepository;
import br.com.Simulavest.domain.questao.Questao;
import br.com.Simulavest.domain.questao.QuestaoRepository;
import br.com.Simulavest.domain.resposta.Resposta;
import br.com.Simulavest.domain.resposta.RespostaRepository;
import br.com.Simulavest.domain.resposta.dto.RespostaDTO;
import br.com.Simulavest.domain.simulado.dto.finalizado.SimuladoFinalizadoDTO;
import br.com.Simulavest.domain.simulado.dto.finalizado.SimuladoResultadoDTO;
import br.com.Simulavest.domain.usuario.Usuario;
import br.com.Simulavest.domain.usuario.UsuarioRepository;
import br.com.Simulavest.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SimuladoFinalizadoService {

    @Autowired private HistoricoRepository historicoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private QuestaoRepository questaoRepository;
    @Autowired private RespostaRepository respostaRepository;
    @Autowired private AlternativaRepository alternativaRepository;

    @Transactional
    public SimuladoResultadoDTO processarSimulado(SimuladoFinalizadoDTO dto) {

        Usuario usuario = usuarioRepository.findById(dto.id_usuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + dto.id_usuario()));

        Historico historico = new Historico();
        historico.setUsuario(usuario);
        historico.setData(LocalDate.now());
        historico.setTipo_simulado(dto.tipo_simulado());
        historico = historicoRepository.save(historico);

        List<Resposta> entidadesResposta = new ArrayList<>();
        int acertos = 0;

        for (RespostaDTO respostaDto : dto.respostas()) {

            Questao questao = questaoRepository.findById(respostaDto.id_questao())
                    .orElseThrow(() -> new ResourceNotFoundException("Questão não encontrada: " + respostaDto.id_questao()));

            Alternativa alternativa = alternativaRepository.findById(respostaDto.id_alternativa_escolhida())
                    .orElseThrow(() -> new ResourceNotFoundException("Alternativa não encontrada"));

            boolean acertou = Boolean.TRUE.equals(alternativa.isCorreta());
            if (acertou) {
                acertos++;
            }

            Resposta novaResposta = new Resposta();
            novaResposta.setHistorico(historico);
            novaResposta.setQuestao(questao);
            novaResposta.setAlternativaEscolhida(alternativa);
            novaResposta.setAcertou(acertou);

            entidadesResposta.add(novaResposta);
        }

        respostaRepository.saveAll(entidadesResposta);


        int totalQuestoes = dto.respostas().size();
        historico.setQuantidade_questoes(totalQuestoes);
        historico.setQuantidade_acertos(acertos);
        historicoRepository.save(historico);

        String aproveitamento = "0.0%";
        if (totalQuestoes > 0) {
            double porc = (double) acertos / totalQuestoes * 100;
            aproveitamento = String.format("%.1f%%", porc);
        }

        return new SimuladoResultadoDTO(
                historico.getId(),
                totalQuestoes,
                acertos,
                totalQuestoes - acertos,
                aproveitamento
        );
    }
}