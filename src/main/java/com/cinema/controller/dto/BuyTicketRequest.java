package com.cinema.controller.dto;

import jakarta.validation.constraints.NotBlank;

/** DTO для запроса покупки билета. */
public record BuyTicketRequest(
        @NotBlank String login
) {
}