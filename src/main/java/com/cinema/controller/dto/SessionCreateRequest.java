package com.cinema.controller.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** DTO для запроса создания сеанса. */
public record SessionCreateRequest(
        @NotBlank String hallCode,
        @NotBlank String filmCode,
        @NotNull LocalDateTime startTime,
        @NotNull @DecimalMin("0.00") BigDecimal basePrice
) {
}