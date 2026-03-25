package com.esieeit.projetsi.domain.exception;

/**
 * Thrown when a request carries logically invalid data.
 */
public class InvalidDataException extends DomainException {

    public InvalidDataException(String message) {
        super(message);
    }

    public static org.springframework.web.server.ResponseStatusException unauthorized(String message) {
        return new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.UNAUTHORIZED,
                message);
    }
}
