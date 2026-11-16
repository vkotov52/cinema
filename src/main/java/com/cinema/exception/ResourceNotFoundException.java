package com.cinema.exception;

/**
 * Выбрасывается, когда запрошенный объект (фильм, зал, сеанс, билет, пользователь...) не найден. HTTP 404.
 */
public class ResourceNotFoundException extends CinemaException {

    public ResourceNotFoundException(String message, String hint) {
        super(message, hint);
    }
}
