package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.conteudo.Conteudo;
import br.com.Vestibuline.domain.conteudo.ConteudoRepository;
import br.com.Vestibuline.domain.instituicao.InstituicaoRepository;
import br.com.Vestibuline.domain.questao.Questao;
import br.com.Vestibuline.domain.questao.QuestaoRepository;
import br.com.Vestibuline.domain.simulado.dto.personalizado.SimuladoPersonalizadoRequestDTO;
import br.com.Vestibuline.domain.simulado.validacoes.ValidadorSimuladoPersonalizado;
import br.com.Vestibuline.exception.RegraDeNegocioException;
import br.com.Vestibuline.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SimuladoPersonalizadoService - Testes Unitários")
class SimuladoPersonalizadoServiceTest {

    @Mock private QuestaoRepository repository;
    @Mock private InstituicaoRepository instituicaoRepository;
    @Mock private ConteudoRepository conteudoRepository;

    private SimuladoPersonalizadoService service;

    @BeforeEach
    void setUp() {
        // Sem validadores registrados: o foco do teste é a lógica de distribuição/erro, não a validação de sigla/instituição.
        service = new SimuladoPersonalizadoService();
        setField("repository", repository);
        setField("instituicaoRepository", instituicaoRepository);
        setField("conteudoRepository", conteudoRepository);
        setField("validadorSimuladoPersonalizados", List.<ValidadorSimuladoPersonalizado>of());
    }

    private void setField(String nome, Object valor) {
        org.springframework.test.util.ReflectionTestUtils.setField(service, nome, valor);
    }

    private Questao questaoCom(String fundamento) {
        Questao questao = new Questao();
        questao.setId(UUID.randomUUID());
        Conteudo conteudo = new Conteudo();
        conteudo.setNome(fundamento);
        questao.setConteudos(List.of(conteudo));
        return questao;
    }

    @Test
    @DisplayName("iniciarSimulado: lança RegraDeNegocioException (não IllegalArgumentException) quando quantidade < nº de fundamentos")
    void iniciarSimulado_lancaRegraDeNegocio_quandoQuantidadeMenorQueFundamentos() {
        var dto = new SimuladoPersonalizadoRequestDTO(List.of("Álgebra", "Geometria", "Trigonometria"), "UFC", 2);

        assertThatThrownBy(() -> service.iniciarSimulado(dto))
                .isInstanceOf(RegraDeNegocioException.class);
    }

    @Test
    @DisplayName("iniciarSimulado: distribui e retorna exatamente a quantidade de questões solicitada quando há questões suficientes")
    void iniciarSimulado_retornaQuantidadeSolicitada_quandoHaQuestoesSuficientes() {
        List<String> fundamentos = List.of("Álgebra", "Geometria");
        var dto = new SimuladoPersonalizadoRequestDTO(fundamentos, "UFC", 4);

        List<Questao> disponiveis = List.of(
                questaoCom("álgebra"), questaoCom("álgebra"), questaoCom("álgebra"),
                questaoCom("geometria"), questaoCom("geometria"), questaoCom("geometria")
        );
        when(repository.buscarQuestoesPorInstituicaoEFundamentos(any(), any())).thenReturn(disponiveis);

        var resultado = service.iniciarSimulado(dto);

        assertThat(resultado).hasSize(4);
    }

    @Test
    @DisplayName("iniciarSimulado: lança ResourceNotFoundException quando não há questões suficientes disponíveis")
    void iniciarSimulado_lancaResourceNotFound_quandoQuestoesInsuficientes() {
        List<String> fundamentos = List.of("Álgebra");
        var dto = new SimuladoPersonalizadoRequestDTO(fundamentos, "UFC", 5);

        when(repository.buscarQuestoesPorInstituicaoEFundamentos(any(), any()))
                .thenReturn(List.of(questaoCom("álgebra"), questaoCom("álgebra")));

        assertThatThrownBy(() -> service.iniciarSimulado(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
