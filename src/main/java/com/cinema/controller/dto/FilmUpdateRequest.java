package com.cinema.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

/** DTO для запроса обновления фильма. */
public record FilmUpdateRequest(
        @NotBlank @Size(max = 200) String title,
        @Min(1) @Max(600) int durationMin,
        @NotBlank String ageRating,
        @NotEmpty Set<String> genreCodes
) {
}