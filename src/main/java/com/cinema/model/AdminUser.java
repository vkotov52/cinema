package com.cinema.model;

public class AdminUser extends User {

    public AdminUser(String login, String password, String lastName, String firstName, String email) {
        super(login, password, lastName, firstName, email);
    }

    @Override
    public UserRole getRole() {
        return UserRole.ADMIN;
    }
}
