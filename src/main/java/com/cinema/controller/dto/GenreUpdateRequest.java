package com.cinema.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTO для запроса обновления жанра. */
public record GenreUpdateRequest(
        @NotBlank @Size(max = 50) String name
) {
}