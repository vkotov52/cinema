package com.cinema.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTO для запроса обновления зала. */
public record HallUpdateRequest(
        @NotBlank @Size(max = 50) String name,
        @Min(1) @Max(50) int rowsCount,
        @Min(1) @Max(60) int seatsPerRow
) {
}