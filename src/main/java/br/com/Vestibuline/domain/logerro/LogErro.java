package br.com.Vestibuline.domain.logerro;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Um erro de produção agrupado por "assinatura" (classe da exceção + rota). Ocorrências
 * repetidas do mesmo erro incrementam {@link #quantidadeOcorrencias} em vez de criar uma
 * linha nova — ver {@link LogErroService}.
 */
@Entity
@Table(name = "log_erro")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class LogErro {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "fingerprint", nullable = false, length = 300)
    private String fingerprint;

    @Column(name = "exception_class", nullable = false)
    private String exceptionClass;

    @Column(name = "http_method", length = 10)
    private String httpMethod;

    @Column(name = "endpoint")
    private String endpoint;

    @Column(name = "status_http")
    private int statusHttp;

    @Enumerated(EnumType.STRING)
    @Column(name = "severidade", nullable = false)
    private Severidade severidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false)
    private CategoriaIncidente categoria;

    @Lob
    @Column(name = "mensagem", columnDefinition = "TEXT")
    private String mensagem;

    @Lob
    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "request_id", length = 100)
    private String requestId;

    @Column(name = "ambiente", length = 50)
    private String ambiente;

    @Column(name = "versao_aplicacao", length = 50)
    private String versaoAplicacao;

    @Column(name = "quantidade_ocorrencias", nullable = false)
    private int quantidadeOcorrencias;

    @Column(name = "primeira_ocorrencia", nullable = false)
    private LocalDateTime primeiraOcorrencia;

    @Column(name = "ultima_ocorrencia", nullable = false)
    private LocalDateTime ultimaOcorrencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusIncidente status;

    @Lob
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "versao_corrigida", length = 50)
    private String versaoCorrigida;

    public void registrarNovaOcorrencia(String mensagem, String stackTrace, UUID usuarioId, String requestId) {
        this.quantidadeOcorrencias++;
        this.ultimaOcorrencia = LocalDateTime.now();
        this.mensagem = mensagem;
        this.stackTrace = stackTrace;
        this.usuarioId = usuarioId;
        this.requestId = requestId;
    }
}
