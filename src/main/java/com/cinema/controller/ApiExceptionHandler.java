package com.cinema.controller;

import com.cinema.exception.BusinessRuleException;
import com.cinema.exception.DuplicateResourceException;
import com.cinema.exception.ErrorResponse;
import com.cinema.exception.InvalidRequestException;
import com.cinema.exception.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 *  Глобальный перехватчик исключений: каждый вид ошибки обрабатывается отдельно
 * и возвращает свой HTTP-статус, описание проблемы и подсказку по исправлению.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException exception) {
        return build(HttpStatus.NOT_FOUND, "Объект не найден", exception.getMessage(), exception.getHint());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException exception) {
        return build(HttpStatus.CONFLICT, "Объект уже существует", exception.getMessage(), exception.getHint());
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(BusinessRuleException exception) {
        return build(HttpStatus.CONFLICT, "Операция невозможна", exception.getMessage(), exception.getHint());
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(InvalidRequestException exception) {
        return build(HttpStatus.BAD_REQUEST, "Некорректный запрос", exception.getMessage(), exception.getHint());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        String fields = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(
                HttpStatus.BAD_REQUEST,
                "Ошибка проверки данных",
                "Некорректные поля запроса: " + fields,
                "Исправьте указанные поля и повторите запрос."
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException exception) {
        return build(
                HttpStatus.BAD_REQUEST,
                "Некорректное тело запроса",
                "Тело запроса отсутствует или не является корректным JSON.",
                "Проверьте синтаксис JSON, формат даты (2026-11-20T12:00:00) и значения перечислений."
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException exception) {
        return build(
                HttpStatus.CONFLICT,
                "Нарушение целостности данных",
                "Операция нарушает ограничения базы данных.",
                "Проверьте уникальность кодов, логина и email, а также существование связанных объектов."
        );
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String message, String hint) {
        return ResponseEntity.status(status).body(new ErrorResponse(status.value(), error, message, hint));
    }
}
