package com.cinema.repository;

import com.cinema.model.User;
import com.cinema.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Репозиторий для работы с сущностью User. */
public interface UserRepository extends JpaRepository<User, String> {

    /** Возвращает список пользователей с заданной ролью (ADMIN, MANAGER или CUSTOMER). */
    List<User> findByRole(UserRole role);
}