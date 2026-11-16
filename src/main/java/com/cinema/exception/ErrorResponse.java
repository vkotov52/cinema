package com.cinema.exception;

/**
 * Единый формат ответа с ошибкой.
 *
 * @param status  HTTP-код
 * @param error   краткое название ошибки
 * @param message что именно произошло
 * @param hint    как исправить
 */
public record ErrorResponse(int status, String error, String message, String hint) {
}
