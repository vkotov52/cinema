package com.cinema.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/** Глобальный перехватчик исключений для всего REST API */
@RestControllerAdvice
public class ApiExceptionHandler {

    /** Обрабатывает отсутствие ресурса и возвращает HTTP 404 */
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFound(NoSuchElementException exception) {
        return Map.of("error", exception.getMessage());
    }

    /** Обрабатывает некорректные аргументы бизнес-логики и возвращает HTTP 400 */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badRequest(IllegalArgumentException exception) {
        return Map.of("error", exception.getMessage());
    }

    /** Обрабатывает конфликты состояний (например, повторная покупка) и возвращает HTTP 409 */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> conflict(IllegalStateException exception) {
        return Map.of("error", exception.getMessage());
    }

    /** Обрабатывает нарушения ограничений БД (уникальность, внешние ключи) и возвращает HTTP 409 */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> integrityViolation(DataIntegrityViolationException exception) {
        return Map.of("error", "Операция нарушает ограничения целостности данных");
    }

    /** Обрабатывает ошибки валидации DTO (@Valid) и возвращает HTTP 400 с деталями по полям */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validation(MethodArgumentNotValidException exception) {
        String details = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return Map.of("error", "Некорректные входные данные: " + details);
    }

    /** Обрабатывает невалидный JSON в теле запроса и возвращает HTTP 400 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> unreadableBody(HttpMessageNotReadableException exception) {
        return Map.of("error", "Тело запроса отсутствует или имеет неверный формат");
    }
}