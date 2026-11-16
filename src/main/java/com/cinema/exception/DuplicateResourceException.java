package com.cinema.exception;

/**
 * Выбрасывается при попытке создать ресурс с уже существующим кодом или логином.
 * Соответствует HTTP 409 Conflict.
 */
public class DuplicateResourceException extends CinemaException {

    public DuplicateResourceException(String message, String hint) {
        super(message, hint);
    }
}
