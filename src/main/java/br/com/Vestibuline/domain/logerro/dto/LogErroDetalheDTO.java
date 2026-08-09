package br.com.Vestibuline.domain.logerro.dto;

import br.com.Vestibuline.domain.logerro.CategoriaIncidente;
import br.com.Vestibuline.domain.logerro.LogErro;
import br.com.Vestibuline.domain.logerro.Severidade;
import br.com.Vestibuline.domain.logerro.StatusIncidente;

import java.time.LocalDateTime;
import java.util.UUID;

public record LogErroDetalheDTO(
        UUID id,
        String fingerprint,
        String exceptionClass,
        String httpMethod,
        String endpoint,
        int statusHttp,
        Severidade severidade,
        CategoriaIncidente categoria,
        String mensagem,
        String stackTrace,
        UUID usuarioId,
        String requestId,
        String ambiente,
        String versaoAplicacao,
        int quantidadeOcorrencias,
        LocalDateTime primeiraOcorrencia,
        LocalDateTime ultimaOcorrencia,
        StatusIncidente status,
        String observacoes,
        String versaoCorrigida
) {
    public LogErroDetalheDTO(LogErro l) {
        this(
                l.getId(), l.getFingerprint(), l.getExceptionClass(), l.getHttpMethod(), l.getEndpoint(),
                l.getStatusHttp(), l.getSeveridade(), l.getCategoria(), l.getMensagem(), l.getStackTrace(),
                l.getUsuarioId(), l.getRequestId(), l.getAmbiente(), l.getVersaoAplicacao(),
                l.getQuantidadeOcorrencias(), l.getPrimeiraOcorrencia(), l.getUltimaOcorrencia(),
                l.getStatus(), l.getObservacoes(), l.getVersaoCorrigida()
        );
    }
}
