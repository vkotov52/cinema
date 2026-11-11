package com.cinema.controller;

import com.cinema.controller.dto.GenreCreateRequest;
import com.cinema.controller.dto.GenreUpdateRequest;
import com.cinema.model.Genre;
import com.cinema.service.GenreService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** REST-контроллер для управления жанрами. Обрабатывает CRUD-операции. */
@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService service;

    /** Возвращает список всех жанров (HTTP 200). */
    @GetMapping
    public List<Genre> findAll() {
        return service.findAll();
    }

    /** Возвращает жанр по его коду или выбрасывает NotFoundException (HTTP 200 / 404). */
    @GetMapping("/{code}")
    public Genre findById(@PathVariable String code) {
        return service.findById(code);
    }

    /** Создаёт новый жанр по данным из запроса и возвращает его (HTTP 201 Created). */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Genre create(@Valid @RequestBody GenreCreateRequest request) {
        return service.create(request.code(), request.name());
    }

    /** Обновляет существующий жанр по коду и возвращает изменённую версию (HTTP 200). */
    @PutMapping("/{code}")
    public Genre update(@PathVariable String code, @Valid @RequestBody GenreUpdateRequest request) {
        return service.update(code, request.name());
    }

    /** Удаляет жанр по коду. Тело ответа пустое (HTTP 204 No Content). */
    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String code) {
        service.delete(code);
    }
}