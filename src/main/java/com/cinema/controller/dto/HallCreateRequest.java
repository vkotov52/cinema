package com.cinema.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** DTO для запроса создания кинозала. */
public record HallCreateRequest(
        @NotBlank
        @Size(max = 10)
        @Pattern(regexp = "[A-Z0-9]+", message = "код зала: только заглавные латинские буквы и цифры")
        String code,
        @NotBlank @Size(max = 50) String name,
        @Min(1) @Max(50) int rowsCount,
        @Min(1) @Max(60) int seatsPerRow
) {
}