package com.cinema.model;

/** Абстрактный пользователь. Роль определяют наследники. */
public abstract class User {

    private final String login;
    private final String password;
    private final String lastName;
    private final String firstName;
    private final String email;

    protected User(String login, String password, String lastName, String firstName, String email) {
        this.login = login;
        this.password = password;
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
    }

    public abstract UserRole getRole();

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }
}
