package com.cinema.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/** JPA-сущность пользователя с ролью ADMIN. */
@Entity
@DiscriminatorValue("ADMIN")
public class AdminUser extends User {

    /** Пустой конструктор без аргументов, требуемый для создания экземпляра при загрузке из БД. */
    protected AdminUser() {
    }

    /** Конструктор для создания нового администратора с заданными данными и фиксированной ролью ADMIN. */
    public AdminUser(String login, String password, String lastName, String firstName, String email) {
        super(login, password, lastName, firstName, email, UserRole.ADMIN);
    }
}