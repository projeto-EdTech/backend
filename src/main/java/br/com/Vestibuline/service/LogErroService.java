package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.logerro.CategoriaIncidente;
import br.com.Vestibuline.domain.logerro.LogErro;
import br.com.Vestibuline.domain.logerro.LogErroRepository;
import br.com.Vestibuline.domain.logerro.Severidade;
import br.com.Vestibuline.domain.logerro.StatusIncidente;
import br.com.Vestibuline.domain.logerro.dto.AtualizarLogErroDTO;
import br.com.Vestibuline.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Persiste erros de produção agrupados por "assinatura" (classe da exceção + rota), para dar
 * aos devs um mapa consultável do que está quebrando, sem depender só de log de arquivo.
 *
 * Nunca deve derrubar a requisição que está sendo tratada: roda de forma assíncrona (thread e
 * transação própria) e qualquer falha na própria gravação do log é apenas logada, nunca lançada.
 */
@Service
public class LogErroService {

    private static final Logger logger = LoggerFactory.getLogger(LogErroService.class);
    private static final Pattern SEGMENTO_ID = Pattern.compile(
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}|\\d+");
    private static final List<StatusIncidente> STATUS_EM_ABERTO = List.of(StatusIncidente.ABERTO, StatusIncidente.EM_ANDAMENTO);

    private final LogErroRepository repository;
    private final Environment environment;

    @Autowired
    public LogErroService(LogErroRepository repository, Environment environment) {
        this.repository = repository;
        this.environment = environment;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(Throwable exception, String httpMethod, String endpoint, int statusHttp,
                           Severidade severidade, CategoriaIncidente categoria, UUID usuarioId, String requestId) {
        try {
            String endpointNormalizado = normalizarEndpoint(endpoint);
            String fingerprint = calcularFingerprint(exception.getClass().getName(), httpMethod, endpointNormalizado);

            LogErro logErro = repository.findFirstByFingerprintAndStatusIn(fingerprint, STATUS_EM_ABERTO)
                    .orElseGet(() -> novoLogErro(fingerprint, exception, httpMethod, endpointNormalizado, statusHttp, severidade, categoria));

            logErro.registrarNovaOcorrencia(mensagemDe(exception), stackTraceDe(exception), usuarioId, requestId);
            repository.save(logErro);
        } catch (Exception falhaAoLogar) {
            // A gravação do log de erro nunca pode gerar um novo erro visível ao usuário.
            logger.warn("Falha ao registrar LogErro (ignorada): {}", falhaAoLogar.toString());
        }
    }

    private LogErro novoLogErro(String fingerprint, Throwable exception, String httpMethod, String endpoint,
                                 int statusHttp, Severidade severidade, CategoriaIncidente categoria) {
        LogErro logErro = new LogErro();
        logErro.setFingerprint(fingerprint);
        logErro.setExceptionClass(exception.getClass().getName());
        logErro.setHttpMethod(httpMethod);
        logErro.setEndpoint(endpoint);
        logErro.setStatusHttp(statusHttp);
        logErro.setSeveridade(severidade);
        logErro.setCategoria(categoria);
        logErro.setAmbiente(ambienteAtual());
        logErro.setQuantidadeOcorrencias(0);
        logErro.setPrimeiraOcorrencia(LocalDateTime.now());
        logErro.setUltimaOcorrencia(LocalDateTime.now());
        logErro.setStatus(StatusIncidente.ABERTO);
        return logErro;
    }

    private String ambienteAtual() {
        String[] perfis = environment.getActiveProfiles();
        return perfis.length > 0 ? String.join(",", perfis) : "default";
    }

    private String normalizarEndpoint(String endpoint) {
        if (endpoint == null) return "desconhecido";
        return SEGMENTO_ID.matcher(endpoint).replaceAll("{id}");
    }

    private String calcularFingerprint(String exceptionClass, String httpMethod, String endpointNormalizado) {
        return exceptionClass + "|" + (httpMethod != null ? httpMethod : "?") + "|" + endpointNormalizado;
    }

    private String mensagemDe(Throwable exception) {
        String mensagem = exception.getMessage();
        return mensagem != null ? mensagem : exception.getClass().getSimpleName();
    }

    @Transactional(readOnly = true)
    public Page<LogErro> listar(StatusIncidente status, Severidade severidade, String ambiente, Pageable pageable) {
        return repository.buscarComFiltros(status, severidade, ambiente, pageable);
    }

    @Transactional(readOnly = true)
    public LogErro buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Log de erro não encontrado: " + id));
    }

    @Transactional
    public LogErro atualizar(UUID id, AtualizarLogErroDTO dto) {
        LogErro logErro = buscarPorId(id);
        logErro.setStatus(dto.status());
        if (dto.observacoes() != null) {
            logErro.setObservacoes(dto.observacoes());
        }
        if (dto.versaoCorrigida() != null) {
            logErro.setVersaoCorrigida(dto.versaoCorrigida());
        }
        return repository.save(logErro);
    }

    private String stackTraceDe(Throwable exception) {
        var writer = new java.io.StringWriter();
        exception.printStackTrace(new java.io.PrintWriter(writer));
        String trace = writer.toString();
        // Evita registros absurdamente grandes em causas encadeadas profundas.
        return trace.length() > 8000 ? trace.substring(0, 8000) + "\n... (truncado)" : trace;
    }
}
