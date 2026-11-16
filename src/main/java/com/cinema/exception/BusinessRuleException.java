package com.cinema.exception;

/**
 * Выбрасывается при нарушении бизнес-правил, например, билет уже продан,
 * сеансы пересекаются). HTTP 409.
 */

public class BusinessRuleException extends CinemaException {

    public BusinessRuleException(String message, String hint) {
        super(message, hint);
    }
}
