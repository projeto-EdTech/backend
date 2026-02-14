package br.com.Simulavest.service;

import br.com.Simulavest.domain.instituicao.Instituicao;
import br.com.Simulavest.domain.instituicao.InstituicaoRepository;
import br.com.Simulavest.domain.nota_corte.NotaCorte;
import br.com.Simulavest.domain.nota_corte.NotaCorteRepository;
import br.com.Simulavest.domain.nota_corte.dto.NotaCorteInputDTO;
import br.com.Simulavest.domain.nota_corte.dto.NotaCorteResponseDTO;
import br.com.Simulavest.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotaCorteService {

    @Autowired
    private NotaCorteRepository notaCorteRepository;

    @Autowired
    private InstituicaoRepository instituicaoRepository;

    @Autowired
    private RotinaLimpezaService limpezaService;

    @Transactional
    public void importarNotas(NotaCorteInputDTO dto) {

        Instituicao instituicao = instituicaoRepository.findBySigla(dto.siglaInstituicao())
                .orElseThrow(() -> new ResourceNotFoundException("Instituição não encontrada: " + dto.siglaInstituicao()));

        List<NotaCorte> notasParaSalvar = dto.cursos().stream()
                .map(cursoDto -> new NotaCorte(
                        instituicao,
                        dto.ano(),
                        cursoDto.nome(),
                        cursoDto.modalidade(),
                        cursoDto.nota()
                ))
                .collect(Collectors.toList());

        notaCorteRepository.saveAll(notasParaSalvar);
        limpezaService.limparNotasCorteAntigas();
    }

    @Transactional
    public NotaCorteResponseDTO buscarNotasCorte(String sigla, String curso) {

        if (curso == null || curso.trim().isEmpty()) {
            throw new ResourceNotFoundException("O curso é obrigatório.");
        }

        if (sigla != null && sigla.trim().isEmpty()) {
            sigla = null;
        }

        List<NotaCorte> listaNotas = notaCorteRepository.listagemNotaCorte(curso, sigla);

        if (listaNotas.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Nenhuma nota encontrada para o curso '" + curso + "'" +
                            (sigla != null ? " na instituição " + sigla : "")
            );
        }

        double mediaSimples = listaNotas.stream()
                .mapToDouble(NotaCorte::getNotaCorte)
                .average()
                .orElse(0.0);

        BigDecimal mediaArredondada = BigDecimal.valueOf(mediaSimples)
                .setScale(2, RoundingMode.HALF_UP);

        return new NotaCorteResponseDTO(
                listaNotas.get(0).getNomeCurso(),
                mediaArredondada.doubleValue(),
                listaNotas.size(),
                sigla != null ? sigla.toUpperCase() : "GERAL"
        );
    }
}
