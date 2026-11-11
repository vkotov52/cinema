package com.cinema.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTO для запроса обновления пользователя. */
public record UserUpdateRequest(
        @NotBlank @Size(max = 100) String password,
        @NotBlank @Size(max = 50) String lastName,
        @NotBlank @Size(max = 50) String firstName,
        @NotBlank @Email @Size(max = 100) String email
) {
}