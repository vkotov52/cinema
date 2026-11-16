package com.cinema.exception;

/**
 * Базовый класс собственных исключений приложения.
 * Кроме описания ошибки хранит подсказку, как её исправить.
 */
public abstract class CinemaException extends RuntimeException {

    private final String hint;

    protected CinemaException(String message, String hint) {
        super(message);
        this.hint = hint;
    }

    public String getHint() {
        return hint;
    }
}
