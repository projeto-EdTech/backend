package br.com.Vestibuline.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestExceptionHandler {

    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var erros = ex.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        return montarResposta(HttpStatus.BAD_REQUEST, "Erro de Validação", erros.toString(), request);
    }

    // 2. Erros de Negócio (Seu Recurso não encontrado)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        return montarResposta(HttpStatus.NOT_FOUND, "Não Encontrado", ex.getMessage(), request);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<Object> handleRegraDeNegocio(RegraDeNegocioException ex, WebRequest request) {
        return montarResposta(HttpStatus.BAD_REQUEST, "Erro de Validação", ex.getMessage(), request);
    }

    // 3. Erros Genéricos (Segurança)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneral(Exception ex, WebRequest request) {
        return montarResposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", ex.getLocalizedMessage(), request);
    }

    // Método auxiliar centralizado
    private ResponseEntity<Object> montarResposta(HttpStatus status, String error, String message, WebRequest request) {
        var body = new ErroResponse(LocalDateTime.now().toString(), status.value(), error, message, request.getDescription(false));
        return new ResponseEntity<>(body, status);
    }

    private record ErroResponse(String timestamp, int status, String error, String message, String path) {}

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        return montarResposta((HttpStatus) ex.getStatusCode(), "Conflito de Dados", ex.getReason(), request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {

        return montarResposta(HttpStatus.BAD_REQUEST, "Parâmetro inválido", ex.getMessage(), request);
    }
}