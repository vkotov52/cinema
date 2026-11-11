package com.cinema.service;

import com.cinema.model.User;
import com.cinema.model.UserRole;

import java.util.List;

/** Сервис для управления пользователями. */
public interface UserService {

    /** Регистрирует нового пользователя с заданной ролью и персональными данными. */
    User register(UserRole role, String login, String password, String lastName, String firstName, String email);

    /** Возвращает список всех пользователей. */
    List<User> findAll();

    /** Возвращает пользователя по логину или выбрасывает NoSuchElementException. */
    User findById(String login);

    /** Возвращает всех пользователей с заданной ролью (ADMIN, MANAGER или CUSTOMER). */
    List<User> findByRole(UserRole role);

    /** Обновляет персональные данные пользователя по логину. Роль остаётся неизменной. */
    User update(String login, String password, String lastName, String firstName, String email);

    /** Удаляет пользователя по логину. Запрещено, если у пользователя есть купленные билеты. */
    void delete(String login);
}