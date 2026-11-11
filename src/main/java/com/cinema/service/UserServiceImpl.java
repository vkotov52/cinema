package com.cinema.service;

import com.cinema.model.AdminUser;
import com.cinema.model.CustomerUser;
import com.cinema.model.ManagerUser;
import com.cinema.model.User;
import com.cinema.model.UserRole;
import com.cinema.repository.TicketRepository;
import com.cinema.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/** Реализация UserService. Содержит бизнес-логику регистрации, поиска, обновления и удаления пользователей. */
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository users;
    private final TicketRepository tickets;

    /** Регистрирует нового пользователя после проверки уникальности логина. Создаёт конкретный подкласс (AdminUser, ManagerUser, CustomerUser) в зависимости от роли. */
    @Override
    public User register(
            UserRole role,
            String login,
            String password,
            String lastName,
            String firstName,
            String email
    ) {
        if (users.existsById(login)) {
            throw new IllegalStateException("Пользователь уже существует: " + login);
        }
        return users.save(createUser(role, login, password, lastName, firstName, email));
    }

    /** Возвращает список всех пользователей. */
    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return users.findAll();
    }

    /** Возвращает пользователя по логину или выбрасывает NoSuchElementException, если пользователь не найден. */
    @Override
    @Transactional(readOnly = true)
    public User findById(String login) {
        return users.findById(login)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден: " + login));
    }

    /** Возвращает всех пользователей с заданной ролью. */
    @Override
    @Transactional(readOnly = true)
    public List<User> findByRole(UserRole role) {
        return users.findByRole(role);
    }

    /** Обновляет персональные данные пользователя по логину. Роль остаётся неизменной. */
    @Override
    public User update(String login, String password, String lastName, String firstName, String email) {
        User user = findById(login);
        user.setPassword(password);
        user.setLastName(lastName);
        user.setFirstName(firstName);
        user.setEmail(email);
        return users.save(user);
    }

    /** Удаляет пользователя по логину. Запрещено, если у пользователя есть купленные билеты. */
    @Override
    public void delete(String login) {
        User user = findById(login);
        if (tickets.existsByCustomer_Login(login)) {
            throw new IllegalStateException("Нельзя удалить пользователя, у которого есть билеты");
        }
        users.delete(user);
    }

    /** Создаёт конкретный подкласс User (AdminUser, ManagerUser или CustomerUser) в зависимости от переданной роли. */
    private User createUser(
            UserRole role,
            String login,
            String password,
            String lastName,
            String firstName,
            String email
    ) {
        if (role == null) {
            throw new IllegalArgumentException("Роль обязательна");
        }
        return switch (role) {
            case ADMIN -> new AdminUser(login, password, lastName, firstName, email);
            case MANAGER -> new ManagerUser(login, password, lastName, firstName, email);
            case CUSTOMER -> new CustomerUser(login, password, lastName, firstName, email);
        };
    }
}