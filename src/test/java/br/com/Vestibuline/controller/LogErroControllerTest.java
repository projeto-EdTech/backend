package br.com.Vestibuline.controller;

import br.com.Vestibuline.domain.logerro.CategoriaIncidente;
import br.com.Vestibuline.domain.logerro.LogErro;
import br.com.Vestibuline.domain.logerro.Severidade;
import br.com.Vestibuline.domain.logerro.StatusIncidente;
import br.com.Vestibuline.domain.logerro.dto.AtualizarLogErroDTO;
import br.com.Vestibuline.infra.security.InternalAccessFilter;
import br.com.Vestibuline.infra.security.SecurityFilter;
import br.com.Vestibuline.service.LogErroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * O gate de segurança de /internal/** (InternalAccessFilter) é testado isoladamente em
 * InternalAccessFilterTest; aqui o foco é o comportamento do controller (filtros/paginação/
 * atualização de status), por isso o filtro real é excluído da fatia de teste.
 */
@WebMvcTest(
        controllers = LogErroController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityFilter.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = InternalAccessFilter.class)
        }
)
@DisplayName("LogErroController - Testes de Integração Web")
class LogErroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LogErroService service;

    private LogErro logErroExemplo() {
        LogErro l = new LogErro();
        l.setId(UUID.randomUUID());
        l.setFingerprint("java.lang.RuntimeException|POST|/simulados/finalizar");
        l.setExceptionClass("java.lang.RuntimeException");
        l.setHttpMethod("POST");
        l.setEndpoint("/simulados/finalizar");
        l.setStatusHttp(500);
        l.setSeveridade(Severidade.ALTA);
        l.setCategoria(CategoriaIncidente.BUG);
        l.setMensagem("boom");
        l.setStackTrace("java.lang.RuntimeException: boom\n\tat ...");
        l.setQuantidadeOcorrencias(3);
        l.setPrimeiraOcorrencia(LocalDateTime.now().minusDays(1));
        l.setUltimaOcorrencia(LocalDateTime.now());
        l.setStatus(StatusIncidente.ABERTO);
        return l;
    }

    @Test
    @DisplayName("GET /internal/logs-erro - lista paginada")
    void listar_retornaPaginaDeLogs() throws Exception {
        LogErro l = logErroExemplo();
        when(service.listar(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(l), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/internal/logs-erro").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].exceptionClass").value("java.lang.RuntimeException"))
                .andExpect(jsonPath("$.content[0].quantidadeOcorrencias").value(3));
    }

    @Test
    @DisplayName("GET /internal/logs-erro/{id} - detalhe inclui stack trace")
    void buscarPorId_retornaDetalheComStackTrace() throws Exception {
        LogErro l = logErroExemplo();
        when(service.buscarPorId(l.getId())).thenReturn(l);

        mockMvc.perform(get("/internal/logs-erro/{id}", l.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stackTrace").value(org.hamcrest.Matchers.containsString("RuntimeException")));
    }

    @Test
    @DisplayName("PATCH /internal/logs-erro/{id} - atualiza status e observações")
    void atualizar_alteraStatus() throws Exception {
        LogErro l = logErroExemplo();
        l.setStatus(StatusIncidente.CORRIGIDO);
        l.setVersaoCorrigida("v1.2.1");
        when(service.atualizar(eq(l.getId()), any(AtualizarLogErroDTO.class))).thenReturn(l);

        var dto = new AtualizarLogErroDTO(StatusIncidente.CORRIGIDO, "Corrigido no PR #42", "v1.2.1");

        mockMvc.perform(patch("/internal/logs-erro/{id}", l.getId())
                        .with(jwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CORRIGIDO"))
                .andExpect(jsonPath("$.versaoCorrigida").value("v1.2.1"));

        verify(service).atualizar(eq(l.getId()), any(AtualizarLogErroDTO.class));
    }
}
