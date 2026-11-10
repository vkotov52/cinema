package com.cinema.model;

public class ManagerUser extends User {

    public ManagerUser(String login, String password, String lastName, String firstName, String email) {
        super(login, password, lastName, firstName, email);
    }

    @Override
    public UserRole getRole() {
        return UserRole.MANAGER;
    }
}
