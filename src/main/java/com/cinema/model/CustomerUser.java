package com.cinema.model;

public class CustomerUser extends User {

    public CustomerUser(String login, String password, String lastName, String firstName, String email) {
        super(login, password, lastName, firstName, email);
    }

    @Override
    public UserRole getRole() {
        return UserRole.CUSTOMER;
    }
}
