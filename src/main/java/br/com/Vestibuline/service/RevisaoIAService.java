package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.alternativa.dto.AlternativaRevisaoDTO;
import br.com.Vestibuline.domain.materia.dto.MateriaRevisaoDTO;
import br.com.Vestibuline.domain.questao.Questao;
import br.com.Vestibuline.domain.questao.QuestaoRepository;
import br.com.Vestibuline.domain.questao.dto.QuestaoRevisaoRecord;
import br.com.Vestibuline.domain.questao.dto.QuestaoRevisaoDTO;
import br.com.Vestibuline.domain.questao.validacoes.ValidadorRevisao;
import br.com.Vestibuline.domain.resposta.RespostaRepository;
import br.com.Vestibuline.domain.usuario.validacoes.ValidadorUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RevisaoIAService {

    private final RespostaRepository respostaRepository;
    private final QuestaoRepository questaoRepository;
    private final ValidadorUsuario validadorUsuario;
    private final ValidadorRevisao validadorRevisao;

    @Transactional(readOnly = true)
    public List<MateriaRevisaoDTO> obterQuestoesParaRevisao(UUID usuarioId) {
        validadorUsuario.validarExistencia(usuarioId);
        List<Object[]> dadosBrutos = respostaRepository.buscarQuestoesEAlternativasParaRevisarPorUsuario(usuarioId);

        if (dadosBrutos == null || dadosBrutos.isEmpty()) {
            return Collections.emptyList();
        }

        List<QuestaoRevisaoRecord> listaDeRegistrosMapeados = dadosBrutos.stream()
                .map(coluna -> new QuestaoRevisaoRecord(
                        (String) coluna[0],
                        (String) coluna[1],
                        (UUID) coluna[2],
                        (UUID) coluna[3],
                        (String) coluna[4],
                        (UUID) coluna[5],
                        (String) coluna[6],
                        (Boolean) coluna[7],
                        (UUID) coluna[8]
                )).toList();

        Map<String, List<QuestaoRevisaoRecord>> questoesAgrupadasPorMateria = listaDeRegistrosMapeados.stream()
                .collect(Collectors.groupingBy(QuestaoRevisaoRecord::nomeMateria));

        List<UUID> idsQuestoes = listaDeRegistrosMapeados.stream()
                .map(QuestaoRevisaoRecord::questaoId)
                .distinct()
                .toList();
        Map<UUID, List<String>> imagensPorQuestao = questaoRepository.findAllById(idsQuestoes).stream()
                .collect(Collectors.toMap(Questao::getId, Questao::getImagens));

        return questoesAgrupadasPorMateria.entrySet().stream()
                .map(materiaEntry -> {
                    String nomeMateria = materiaEntry.getKey();
                    Map<UUID, List<QuestaoRevisaoRecord>> porQuestaoId = materiaEntry.getValue().stream()
                            .collect(Collectors.groupingBy(QuestaoRevisaoRecord::questaoId));

                    List<QuestaoRevisaoDTO> questoesMontadas = porQuestaoId.entrySet().stream()
                            .map(questaoEntry -> {
                                List<QuestaoRevisaoRecord> listaAlternativas = questaoEntry.getValue();
                                QuestaoRevisaoRecord dadosGerais = listaAlternativas.get(0);

                                java.util.Collection<AlternativaRevisaoDTO> alternativasAgrupadasPorQuestaoId = listaAlternativas.stream()
                                        .collect(Collectors.toMap(
                                                QuestaoRevisaoRecord::alternativaId,
                                                r -> new AlternativaRevisaoDTO(
                                                        r.alternativaId(),
                                                        r.textoAlternativa(),
                                                        r.correta(),
                                                        r.alternativaId().equals(r.alternativaEscolhidaId())
                                                ),
                                                (existente, nova) -> nova.escolhida() ? nova : existente
                                        )).values();

                                List<AlternativaRevisaoDTO> mapaDeAlternativasUnicas = new java.util.ArrayList<>(alternativasAgrupadasPorQuestaoId);

                                return new QuestaoRevisaoDTO(
                                        dadosGerais.questaoId(),
                                        dadosGerais.nomeFundamento(),
                                        dadosGerais.enunciado(),
                                        mapaDeAlternativasUnicas,
                                        imagensPorQuestao.getOrDefault(dadosGerais.questaoId(), List.of())
                                );
                            }).collect(Collectors.toList());

                    return new MateriaRevisaoDTO(nomeMateria, questoesMontadas);
                }).collect(Collectors.toList());
    }

    @Transactional
    public void marcarComoRevisado(UUID usuarioId, UUID questaoId) {
        validadorUsuario.validarExistencia(usuarioId);

        validadorRevisao.validarExistenciaDeRevisaoPendente(usuarioId, questaoId);

        respostaRepository.marcarQuestaoComoRevisada(usuarioId, questaoId);
    }
}
