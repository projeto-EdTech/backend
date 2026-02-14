package br.com.Simulavest.service;

import br.com.Simulavest.domain.flashcard.dto.FlashCardDTO;
import br.com.Simulavest.domain.historico.dto.MateriaDesempenhoDTO;
import br.com.Simulavest.domain.historico.dto.TopicoDTO;
import br.com.Simulavest.domain.resposta.RespostaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FlashCardService {

    @Autowired
    private RespostaRepository repository;

    @Transactional(readOnly = true)
    public FlashCardDTO gerarRecomendacao(UUID userId) {

        var listaBruta = repository.buscarTodosErrosPorMateriaETopico(userId);

        Map<String, List<RespostaRepository.ResumoErroProjection>> agrupadoPorMateria = listaBruta.stream()
                .collect(Collectors.groupingBy(RespostaRepository.ResumoErroProjection::getMateria));

        List<MateriaDesempenhoDTO> listaMaterias = new ArrayList<>();

        agrupadoPorMateria.forEach((nomeMateria, listaDeErrosDaMateria) -> {

            List<TopicoDTO> top10Topicos = listaDeErrosDaMateria.stream()
                    .sorted((a, b) -> b.getQtdErros().compareTo(a.getQtdErros()))
                    .limit(10)
                    .map(p -> new TopicoDTO(p.getTopico(), p.getQtdErros()))
                    .collect(Collectors.toList());

            listaMaterias.add(new MateriaDesempenhoDTO(nomeMateria, top10Topicos));
        });

        return new FlashCardDTO(listaMaterias);
    }
}
