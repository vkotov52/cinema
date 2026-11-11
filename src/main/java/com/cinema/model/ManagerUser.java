package com.cinema.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/** JPA-сущность пользователя с ролью MANAGER. */
@Entity
@DiscriminatorValue("MANAGER")
public class ManagerUser extends User {

    /** Пустой конструктор без аргументов, требуемый для создания экземпляра при загрузке из БД. */
    protected ManagerUser() {
    }

    /** Конструктор для создания нового менеджера с заданными данными и фиксированной ролью MANAGER. */
    public ManagerUser(String login, String password, String lastName, String firstName, String email) {
        super(login, password, lastName, firstName, email, UserRole.MANAGER);
    }
}