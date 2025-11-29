package ar.edu.unlam.parcial.exception;

/**
 * Excepción personalizada para secuencias de ADN inválidas.
 * @author Cecilia Calvo - Legajo 46332
 */
public class InvalidDnaException extends RuntimeException {
    public InvalidDnaException(String message) {
        super(message);
    }

    public InvalidDnaException(String message, Throwable cause) {
        super(message, cause);
    }
}