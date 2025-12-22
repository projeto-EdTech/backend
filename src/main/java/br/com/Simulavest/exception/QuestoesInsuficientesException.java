package br.com.Simulavest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Esta exceção será lançada quando a busca por questões não retornar a quantidade mínima esperada.
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class QuestoesInsuficientesException extends RuntimeException {
    public QuestoesInsuficientesException(String message) {
        super(message);
    }
}
