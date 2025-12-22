package br.com.Simulavest.domain.simulado;

import br.com.Simulavest.domain.alternativa.dto.AlternativaSimuladoPersonalizadoDTO;
import br.com.Simulavest.domain.questao.Questao;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record SimuladoPersonalizadoDTO(
        UUID id,
        String enunciado,
        List<AlternativaSimuladoPersonalizadoDTO> alternativas,
        List<String> imagens

) {
    public SimuladoPersonalizadoDTO(Questao questao) {
        this(
                questao.getId(),
                questao.getEnunciado(),
                questao.getAlternativas().stream()
                        .map(AlternativaSimuladoPersonalizadoDTO::new)
                        .collect(Collectors.toList()),
                questao.getImagens()
        );
    }
}
