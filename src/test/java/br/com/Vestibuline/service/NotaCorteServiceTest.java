package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.instituicao.InstituicaoRepository;
import br.com.Vestibuline.domain.nota_corte.NotaCorte;
import br.com.Vestibuline.domain.nota_corte.NotaCorteRepository;
import br.com.Vestibuline.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotaCorteService - Testes Unitários")
class NotaCorteServiceTest {

    @Mock private NotaCorteRepository notaCorteRepository;
    @Mock private InstituicaoRepository instituicaoRepository;

    @InjectMocks
    private NotaCorteService service;

    private NotaCorte notaCorte(String curso, double nota) {
        return new NotaCorte(null, 2025, curso, "AC", nota);
    }

    @Test
    @DisplayName("buscarNotasCorte: sigla nula (busca sem filtro de instituição) não lança NPE e retorna instituicao=null")
    void buscarNotasCorte_semSigla_naoLancaNPE() {
        when(notaCorteRepository.listagemNotaCorte(eq("Medicina"), eq(null)))
                .thenReturn(List.of(notaCorte("MEDICINA", 900.0)));

        var resultado = service.buscarNotasCorte(null, "Medicina");

        assertThat(resultado.instituicao()).isNull();
        assertThat(resultado.mediaNotaCorte()).isEqualTo(900.0);
    }

    @Test
    @DisplayName("buscarNotasCorte: sigla em branco é normalizada para null antes da busca")
    void buscarNotasCorte_siglaEmBranco_normalizaParaNull() {
        when(notaCorteRepository.listagemNotaCorte(eq("Medicina"), eq(null)))
                .thenReturn(List.of(notaCorte("MEDICINA", 900.0)));

        var resultado = service.buscarNotasCorte("   ", "Medicina");

        assertThat(resultado.instituicao()).isNull();
    }

    @Test
    @DisplayName("buscarNotasCorte: sigla informada é normalizada para maiúsculas na resposta")
    void buscarNotasCorte_comSigla_retornaSiglaEmMaiusculas() {
        when(notaCorteRepository.listagemNotaCorte(eq("Medicina"), eq("ufc")))
                .thenReturn(List.of(notaCorte("MEDICINA", 850.0)));

        var resultado = service.buscarNotasCorte("ufc", "Medicina");

        assertThat(resultado.instituicao()).isEqualTo("UFC");
    }

    @Test
    @DisplayName("buscarNotasCorte: nenhuma nota encontrada lança ResourceNotFoundException (404), não NPE")
    void buscarNotasCorte_semResultados_lancaResourceNotFound() {
        when(notaCorteRepository.listagemNotaCorte(eq("Curso Inexistente"), eq(null)))
                .thenReturn(List.of());

        assertThatThrownBy(() -> service.buscarNotasCorte(null, "Curso Inexistente"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
