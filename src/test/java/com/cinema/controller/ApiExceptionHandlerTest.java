package com.cinema.controller;

import com.cinema.exception.BusinessRuleException;
import com.cinema.exception.DuplicateResourceException;
import com.cinema.exception.ErrorResponse;
import com.cinema.exception.InvalidRequestException;
import com.cinema.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Проверяет, что {@link ApiExceptionHandler} преобразует каждое собственное исключение
 * в ответ с корректным HTTP-статусом и единым форматом {@link ErrorResponse}.
 */
@SpringBootTest(classes = ApiExceptionHandler.class)
class ApiExceptionHandlerTest {

    @Autowired
    private ApiExceptionHandler handler;

    @Test
    void handleNotFoundReturns404() {
        ResourceNotFoundException exception = new ResourceNotFoundException(
                "Фильм не найден: F999",
                "Проверьте код фильма. Список фильмов: GET /api/films."
        );

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(404, body.status());
        assertEquals("Объект не найден", body.error());
        assertEquals("Фильм не найден: F999", body.message());
        assertEquals("Проверьте код фильма. Список фильмов: GET /api/films.", body.hint());
    }

    @Test
    void handleDuplicateReturns409() {
        DuplicateResourceException exception = new DuplicateResourceException(
                "Жанр уже существует: DRAMA",
                "Выберите другой код жанра."
        );

        ResponseEntity<ErrorResponse> response = handler.handleDuplicate(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(409, body.status());
        assertEquals("Объект уже существует", body.error());
        assertEquals("Жанр уже существует: DRAMA", body.message());
        assertEquals("Выберите другой код жанра.", body.hint());
    }

    @Test
    void handleBusinessRuleReturns409() {
        BusinessRuleException exception = new BusinessRuleException(
                "Билет уже продан или недоступен",
                "Выберите другой свободный билет."
        );

        ResponseEntity<ErrorResponse> response = handler.handleBusinessRule(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(409, body.status());
        assertEquals("Операция невозможна", body.error());
        assertEquals("Билет уже продан или недоступен", body.message());
        assertEquals("Выберите другой свободный билет.", body.hint());
    }

    @Test
    void handleInvalidRequestReturns400() {
        InvalidRequestException exception = new InvalidRequestException(
                "Недопустимый возрастной рейтинг: 99+",
                "Допустимые значения: 0+, 6+, 12+, 16+, 18+."
        );

        ResponseEntity<ErrorResponse> response = handler.handleInvalidRequest(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.status());
        assertEquals("Некорректный запрос", body.error());
        assertEquals("Недопустимый возрастной рейтинг: 99+", body.message());
        assertEquals("Допустимые значения: 0+, 6+, 12+, 16+, 18+.", body.hint());
    }
}