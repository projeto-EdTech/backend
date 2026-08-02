package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.logerro.CategoriaIncidente;
import br.com.Vestibuline.domain.logerro.LogErro;
import br.com.Vestibuline.domain.logerro.LogErroRepository;
import br.com.Vestibuline.domain.logerro.Severidade;
import br.com.Vestibuline.domain.logerro.StatusIncidente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LogErroService - Testes Unitários")
class LogErroServiceTest {

    @Mock private LogErroRepository repository;
    @Mock private Environment environment;

    private LogErroService service;

    private void setUp() {
        service = new LogErroService(repository, environment);
    }

    @Test
    @DisplayName("primeira ocorrência de um erro cria um novo registro com quantidade=1")
    void primeiraOcorrencia_criaNovoRegistro() {
        setUp();
        when(environment.getActiveProfiles()).thenReturn(new String[]{"default"});
        when(repository.findFirstByFingerprintAndStatusIn(anyString(), anyList())).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RuntimeException erro = new RuntimeException("Falha ao gerar simulado");
        UUID usuarioId = UUID.randomUUID();

        service.registrar(erro, "POST", "/simulados/finalizar", 500, Severidade.ALTA, CategoriaIncidente.BUG, usuarioId, "req-1");

        ArgumentCaptor<LogErro> captor = ArgumentCaptor.forClass(LogErro.class);
        verify(repository).save(captor.capture());

        LogErro salvo = captor.getValue();
        assertThat(salvo.getQuantidadeOcorrencias()).isEqualTo(1);
        assertThat(salvo.getStatus()).isEqualTo(StatusIncidente.ABERTO);
        assertThat(salvo.getExceptionClass()).isEqualTo("java.lang.RuntimeException");
        assertThat(salvo.getUsuarioId()).isEqualTo(usuarioId);
        assertThat(salvo.getMensagem()).isEqualTo("Falha ao gerar simulado");
    }

    @Test
    @DisplayName("segunda ocorrência do mesmo erro (mesma assinatura) incrementa em vez de duplicar")
    void segundaOcorrencia_incrementaRegistroExistente() {
        setUp();
        when(environment.getActiveProfiles()).thenReturn(new String[]{"default"});

        LogErro existente = new LogErro();
        existente.setQuantidadeOcorrencias(1);
        existente.setStatus(StatusIncidente.ABERTO);

        when(repository.findFirstByFingerprintAndStatusIn(anyString(), anyList())).thenReturn(Optional.of(existente));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RuntimeException erro = new RuntimeException("Falha ao gerar simulado");

        service.registrar(erro, "POST", "/simulados/finalizar", 500, Severidade.ALTA, CategoriaIncidente.BUG, null, "req-2");

        ArgumentCaptor<LogErro> captor = ArgumentCaptor.forClass(LogErro.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getQuantidadeOcorrencias()).isEqualTo(2);
        assertThat(captor.getValue()).isSameAs(existente);
    }

    @Test
    @DisplayName("endpoints com UUID diferentes na mesma rota geram a mesma assinatura (normalização de path)")
    void enderecosComIdsDiferentes_geramMesmaAssinatura() {
        setUp();
        when(environment.getActiveProfiles()).thenReturn(new String[]{"default"});
        when(repository.findFirstByFingerprintAndStatusIn(anyString(), anyList())).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RuntimeException erro = new RuntimeException("boom");
        String id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();

        service.registrar(erro, "PATCH", "/api/revisao/usuarios/questoes/" + id1 + "/revisado",
                500, Severidade.ALTA, CategoriaIncidente.BUG, null, "req-3");
        service.registrar(erro, "PATCH", "/api/revisao/usuarios/questoes/" + id2 + "/revisado",
                500, Severidade.ALTA, CategoriaIncidente.BUG, null, "req-4");

        ArgumentCaptor<String> fingerprints = ArgumentCaptor.forClass(String.class);
        verify(repository, times(2)).findFirstByFingerprintAndStatusIn(fingerprints.capture(), anyList());

        assertThat(fingerprints.getAllValues().get(0)).isEqualTo(fingerprints.getAllValues().get(1));
    }

    @Test
    @DisplayName("erro já CORRIGIDO/VALIDADO não é reaproveitado — recorrência cria assinatura ainda buscada só entre status em aberto")
    void buscaApenasEntreStatusEmAberto() {
        setUp();
        when(environment.getActiveProfiles()).thenReturn(new String[]{"default"});
        when(repository.findFirstByFingerprintAndStatusIn(anyString(), anyList())).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.registrar(new RuntimeException("x"), "GET", "/qualquer", 500,
                Severidade.ALTA, CategoriaIncidente.BUG, null, "req-5");

        ArgumentCaptor<List<StatusIncidente>> statusCaptor = ArgumentCaptor.forClass(List.class);
        verify(repository).findFirstByFingerprintAndStatusIn(anyString(), statusCaptor.capture());
        assertThat(statusCaptor.getValue()).containsExactlyInAnyOrder(StatusIncidente.ABERTO, StatusIncidente.EM_ANDAMENTO);
    }

    @Test
    @DisplayName("falha ao salvar o log de erro não propaga exceção (nunca deve derrubar a requisição original)")
    void falhaAoSalvar_naoPropagaExcecao() {
        setUp();
        when(repository.findFirstByFingerprintAndStatusIn(anyString(), anyList()))
                .thenThrow(new RuntimeException("banco indisponível"));

        org.assertj.core.api.Assertions.assertThatCode(() ->
                service.registrar(new RuntimeException("x"), "GET", "/qualquer", 500,
                        Severidade.ALTA, CategoriaIncidente.BUG, null, "req-6")
        ).doesNotThrowAnyException();
    }
}
