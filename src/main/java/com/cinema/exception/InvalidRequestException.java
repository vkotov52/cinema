package com.cinema.exception;

/**
 * Выбрасывается, когда входные данные запроса некорректны с точки зрения бизнес-логики. HTTP 400.
 */
public class InvalidRequestException extends CinemaException {

    public InvalidRequestException(String message, String hint) {
        super(message, hint);
    }
}
