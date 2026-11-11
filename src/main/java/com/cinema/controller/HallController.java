package com.cinema.controller;

import com.cinema.controller.dto.HallCreateRequest;
import com.cinema.controller.dto.HallUpdateRequest;
import com.cinema.model.Hall;
import com.cinema.service.HallService;
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

/** REST-контроллер для управления кинозалами. Обрабатывает CRUD-операции. */
@RestController
@RequestMapping("/api/halls")
@RequiredArgsConstructor
public class HallController {

    private final HallService service;

    /** Возвращает список всех залов (HTTP 200). */
    @GetMapping
    public List<Hall> findAll() {
        return service.findAll();
    }

    /** Возвращает зал по его коду или выбрасывает NotFoundException (HTTP 200 / 404). */
    @GetMapping("/{code}")
    public Hall findById(@PathVariable String code) {
        return service.findById(code);
    }

    /** Создаёт новый зал по данным из запроса и возвращает его (HTTP 201 Created). */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Hall create(@Valid @RequestBody HallCreateRequest request) {
        return service.create(request.code(), request.name(), request.rowsCount(), request.seatsPerRow());
    }

    /** Обновляет существующий зал по коду и возвращает изменённую версию (HTTP 200). */
    @PutMapping("/{code}")
    public Hall update(@PathVariable String code, @Valid @RequestBody HallUpdateRequest request) {
        return service.update(code, request.name(), request.rowsCount(), request.seatsPerRow());
    }

    /** Удаляет зал по коду. Тело ответа пустое (HTTP 204 No Content). */
    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String code) {
        service.delete(code);
    }
}