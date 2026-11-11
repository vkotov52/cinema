package com.cinema.controller.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/** DTO для запроса обновления сеанса. */
public record SessionUpdateRequest(
        @NotBlank String filmCode,
        @NotNull @DecimalMin("0.00") BigDecimal basePrice
) {
}