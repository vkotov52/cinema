package com.cinema.controller;

import com.cinema.controller.dto.UserCreateRequest;
import com.cinema.controller.dto.UserUpdateRequest;
import com.cinema.model.User;
import com.cinema.model.UserRole;
import com.cinema.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** REST-контроллер для управления пользователями. Обрабатывает CRUD-операции и фильтрацию по роли. */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    /** Возвращает список всех пользователей или фильтрует по роли ADMIN/MANAGER/CUSTOMER (HTTP 200). */
    @GetMapping
    public List<User> findAll(@RequestParam(required = false) UserRole role) {
        return role == null ? service.findAll() : service.findByRole(role);
    }

    /** Возвращает пользователя по логину или выбрасывает NotFoundException (HTTP 200 / 404). */
    @GetMapping("/{login}")
    public User findById(@PathVariable String login) {
        return service.findById(login);
    }

    /** Регистрирует нового пользователя с заданной ролью и возвращает его (HTTP 201 Created). */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody UserCreateRequest request) {
        return service.register(
                request.role(),
                request.login(),
                request.password(),
                request.lastName(),
                request.firstName(),
                request.email()
        );
    }

    /** Обновляет данные существующего пользователя по логину и возвращает изменённую версию (HTTP 200). */
    @PutMapping("/{login}")
    public User update(@PathVariable String login, @Valid @RequestBody UserUpdateRequest request) {
        return service.update(
                login,
                request.password(),
                request.lastName(),
                request.firstName(),
                request.email()
        );
    }

    /** Удаляет пользователя по логину. Тело ответа пустое (HTTP 204 No Content). */
    @DeleteMapping("/{login}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String login) {
        service.delete(login);
    }
}