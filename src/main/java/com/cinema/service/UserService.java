package com.cinema.service;

import com.cinema.model.User;
import com.cinema.model.UserRole;

import java.util.List;

public interface UserService {

    /** Регистрирует пользователя: по роли создаётся AdminUser, ManagerUser или CustomerUser. */
    User register(UserRole role, String login, String password, String lastName, String firstName, String email);

    List<User> findAll();

    List<User> findByRole(UserRole role);

    /** Удаляет пользователя. Нельзя удалить пользователя, у которого есть билеты. */
    void delete(String login);
}
