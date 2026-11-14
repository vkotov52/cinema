package com.cinema.controller.dto;

import com.cinema.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** DTO для запроса создания пользователя. */
public record UserCreateRequest(
        @NotBlank
        @Size(max = 30)
        @Pattern(regexp = "\\S+", message = "логин не должен содержать пробелов")
        String login,
        @NotBlank @Size(max = 100) String password,
        @NotBlank @Size(max = 50) String lastName,
        @NotBlank @Size(max = 50) String firstName,
        @NotBlank @Email @Size(max = 100) String email,
        @NotNull UserRole role
) {
}