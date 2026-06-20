package br.com.Vestibuline.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        var erros = ex.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        return montarResposta(HttpStatus.BAD_REQUEST, "Erro de Validação", "Campos inválidos: " + erros, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        return montarResposta(HttpStatus.NOT_FOUND, "Não Encontrado", ex.getMessage(), request);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<Object> handleRegraDeNegocio(RegraDeNegocioException ex, WebRequest request) {
        return montarResposta(HttpStatus.BAD_REQUEST, "Regra de Negócio Violada", ex.getMessage(), request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        return montarResposta((HttpStatus) ex.getStatusCode(), "Conflito de Dados", ex.getReason(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneral(Exception ex, WebRequest request) {
        log.error("Erro interno detectado na API: ", ex);

        String mensagemSegura = "Ocorreu um erro interno inesperado no servidor. Por favor, tente novamente mais tarde.";
        return montarResposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", mensagemSegura, request);
    }

    private ResponseEntity<Object> montarResposta(HttpStatus status, String error, String message, WebRequest request) {
        var body = new ErroResponse(LocalDateTime.now().toString(), status.value(), error, message, request.getDescription(false));
        return new ResponseEntity<>(body, status);
    }

    private record ErroResponse(String timestamp, int status, String error, String message, String path) {}
}