package br.com.Vestibuline.exception;

import br.com.Vestibuline.domain.logerro.CategoriaIncidente;
import br.com.Vestibuline.domain.logerro.Severidade;
import br.com.Vestibuline.domain.usuario.Usuario;
import br.com.Vestibuline.service.LogErroService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    private final LogErroService logErroService;

    public RestExceptionHandler(LogErroService logErroService) {
        this.logErroService = logErroService;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        String mensagemErro = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Erro de validação nos campos.");

        return montarResposta(HttpStatus.BAD_REQUEST, "Erro de Validação", mensagemErro, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        return montarResposta(HttpStatus.NOT_FOUND, "Não Encontrado", ex.getMessage(), request);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<Object> handleRegraDeNegocio(RegraDeNegocioException ex, WebRequest request) {
        return montarResposta(HttpStatus.BAD_REQUEST, "Erro de Validação", ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        return montarResposta(HttpStatus.BAD_REQUEST, "Erro de Validação", ex.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        logger.warn("Violação de integridade de dados: {}", ex.getMessage());
        registrarLogErro(ex, request, HttpStatus.CONFLICT, Severidade.MEDIA, CategoriaIncidente.BANCO_DADOS);
        return montarResposta(HttpStatus.CONFLICT, "Conflito de Dados",
                "Os dados enviados violam uma regra de integridade (ex.: registro duplicado ou referência inválida).", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneral(Exception ex, WebRequest request) {
        logger.error("Erro não tratado ao processar requisição", ex);
        registrarLogErro(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, Severidade.ALTA, CategoriaIncidente.BUG);
        return montarResposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.", request);
    }

    private ResponseEntity<Object> montarResposta(HttpStatus status, String error, String message, WebRequest request) {
        var body = new ErroResponse(LocalDateTime.now().toString(), status.value(), error, message,
                request.getDescription(false), MDC.get("requestId"));
        return new ResponseEntity<>(body, status);
    }

    /**
     * Persiste erros de produção agrupados por assinatura, para consulta pelos devs via
     * {@link br.com.Vestibuline.controller.LogErroController}. Só chamado para erros que
     * realmente merecem investigação (5xx e violação de integridade) — não para validação de
     * input do cliente, que é comportamento esperado, não incidente.
     */
    private void registrarLogErro(Throwable ex, WebRequest request, HttpStatus status,
                                   Severidade severidade, CategoriaIncidente categoria) {
        HttpServletRequest servletRequest = request instanceof ServletWebRequest servletWebRequest
                ? servletWebRequest.getRequest()
                : null;

        String metodo = servletRequest != null ? servletRequest.getMethod() : null;
        String endpoint = servletRequest != null ? servletRequest.getRequestURI() : request.getDescription(false);

        logErroService.registrar(ex, metodo, endpoint, status.value(), severidade, categoria,
                usuarioAutenticadoId(), MDC.get("requestId"));
    }

    private UUID usuarioAutenticadoId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Usuario usuario) {
            return usuario.getId();
        }
        return null;
    }

    private record ErroResponse(String timestamp, int status, String error, String message, String path, String requestId) {}

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        return montarResposta((HttpStatus) ex.getStatusCode(), "Conflito de Dados", ex.getReason(), request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {

        return montarResposta(HttpStatus.BAD_REQUEST, "Parâmetro inválido", ex.getMessage(), request);
    }
}