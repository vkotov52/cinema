package com.cinema.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/** JPA-сущность пользователя с ролью CUSTOMER. */
@Entity
@DiscriminatorValue("CUSTOMER")
public class CustomerUser extends User {

    /** Пустой конструктор без аргументов, требуемый для создания экземпляра при загрузке из БД. */
    protected CustomerUser() {
    }

    /** Конструктор для создания нового покупателя с заданными данными и фиксированной ролью CUSTOMER. */
    public CustomerUser(String login, String password, String lastName, String firstName, String email) {
        super(login, password, lastName, firstName, email, UserRole.CUSTOMER);
    }
}