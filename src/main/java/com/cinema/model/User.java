package com.cinema.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Абстрактная JPA-сущность пользователя. Таблица: user_account. */
@Entity
@Table(name = "user_account")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING, length = 10)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class User {

    /** Первичный ключ — уникальный логин пользователя (до 30 символов). */
    @Id
    @Column(name = "login", length = 30)
    @NotBlank
    private String login;

    /** Хеш пароля. Аннотация WRITE_ONLY скрывает поле в JSON-ответах для безопасности. */
    @Setter
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password_hash", nullable = false, length = 100)
    @NotBlank
    private String password;

    /** Фамилия пользователя, до 50 символов. */
    @Setter
    @Column(name = "last_name", nullable = false, length = 50)
    @NotBlank
    private String lastName;

    /** Имя пользователя, до 50 символов. */
    @Setter
    @Column(name = "first_name", nullable = false, length = 50)
    @NotBlank
    private String firstName;

    /** Уникальный email-адрес пользователя в корректном формате. */
    @Setter
    @Column(name = "email", nullable = false, unique = true, length = 100)
    @Email
    @NotBlank
    private String email;

    /** Роль пользователя (ADMIN, MANAGER, CUSTOMER). */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", insertable = false, updatable = false, length = 10)
    private UserRole role;

    /** Защищенный конструктор для инициализации полей в классах-наследниках (AdminUser, ManagerUser, CustomerUser). */
    protected User(
            String login,
            String password,
            String lastName,
            String firstName,
            String email,
            UserRole role
    ) {
        this.login = login;
        this.password = password;
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.role = role;
    }
}