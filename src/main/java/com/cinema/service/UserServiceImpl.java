package com.cinema.service;

import com.cinema.exception.BusinessRuleException;
import com.cinema.exception.DuplicateResourceException;
import com.cinema.exception.InvalidRequestException;
import com.cinema.exception.ResourceNotFoundException;
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
            throw new DuplicateResourceException(
                    "Пользователь уже существует: " + login,
                    "Выберите другой логин."
            );
        }
        return users.save(createUser(role, login, password, lastName, firstName, email));
    }

    /** Возвращает список всех пользователей. */
    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return users.findAll();
    }

    /** Возвращает пользователя по логину или выбрасывает ResourceNotFoundException, если пользователь не найден. */
    @Override
    @Transactional(readOnly = true)
    public User findById(String login) {
        return users.findById(login)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Пользователь не найден: " + login,
                        "Проверьте логин. Список пользователей: GET /api/users."
                ));
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
            throw new BusinessRuleException(
                    "Нельзя удалить пользователя, у которого есть билеты",
                    "Сначала верните все билеты."
            );
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
            throw new InvalidRequestException(
                    "Роль обязательна",
                    "Укажите роль: ADMIN, MANAGER или CUSTOMER."
            );
        }
        return switch (role) {
            case ADMIN -> new AdminUser(login, password, lastName, firstName, email);
            case MANAGER -> new ManagerUser(login, password, lastName, firstName, email);
            case CUSTOMER -> new CustomerUser(login, password, lastName, firstName, email);
        };
    }
}
