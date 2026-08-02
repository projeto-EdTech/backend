package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.alternativa.dto.AlternativaDTO;
import br.com.Vestibuline.domain.instituicao.Instituicao;
import br.com.Vestibuline.domain.instituicao.InstituicaoRepository;
import br.com.Vestibuline.domain.materia.MateriaRepository;
import br.com.Vestibuline.domain.prova.dto.ProvaDTO;
import br.com.Vestibuline.domain.questao.QuestaoRepository;
import br.com.Vestibuline.domain.questao.dto.QuestaoDTO;
import br.com.Vestibuline.exception.RegraDeNegocioException;
import br.com.Vestibuline.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("InstituicaoService - Testes Unitários (adicionarProva)")
class InstituicaoServiceTest {

    @Mock private InstituicaoRepository repository;
    @Mock private MateriaRepository materiaRepository;
    @Mock private QuestaoRepository questaoRepository;
    @Mock private MateriaService materiaService;
    @Mock private ConteudoService conteudoService;

    @InjectMocks
    private InstituicaoService service;

    private ProvaDTO provaComConteudo(String conteudoMalFormado) {
        var alternativa = new AlternativaDTO("A", "texto");
        var questao = new QuestaoDTO(1, "enunciado", List.of(alternativa), "A", List.of(conteudoMalFormado), List.of());
        return new ProvaDTO("UFC", "UFC", "Prova 2025", 2025, 1, 1, List.of(questao));
    }

    @Test
    @DisplayName("adicionarProva: instituição inexistente lança ResourceNotFoundException (não IllegalArgumentException genérica)")
    void adicionarProva_lancaResourceNotFound_quandoInstituicaoNaoExiste() {
        var dto = provaComConteudo("Matemática - Álgebra");
        when(repository.findBySigla("UFC")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.adicionarProva(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("adicionarProva: conteúdo sem separador \"Matéria - Conteúdo\" lança RegraDeNegocioException em vez de ArrayIndexOutOfBoundsException mascarada")
    void adicionarProva_lancaRegraDeNegocio_quandoConteudoMalFormado() {
        var dto = provaComConteudo("ConteudoSemSeparador");
        when(repository.findBySigla("UFC")).thenReturn(Optional.of(new Instituicao()));

        assertThatThrownBy(() -> service.adicionarProva(dto))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Formato de conteúdo inválido");
    }
}
