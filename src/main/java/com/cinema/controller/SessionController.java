package com.cinema.controller;

import com.cinema.controller.dto.SessionCreateRequest;
import com.cinema.controller.dto.SessionUpdateRequest;
import com.cinema.model.FilmSession;
import com.cinema.service.SessionService;
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

/** REST-контроллер для управления сеансами. Обрабатывает CRUD-операции и фильтрацию по залу. */
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService service;

    /** Возвращает список всех сеансов или фильтрует по коду зала (HTTP 200). */
    @GetMapping
    public List<FilmSession> findAll(@RequestParam(required = false) String hallCode) {
        return hallCode == null ? service.findAll() : service.findByHall(hallCode);
    }

    /** Возвращает сеанс по его коду или выбрасывает NotFoundException (HTTP 200 / 404). */
    @GetMapping("/{code}")
    public FilmSession findById(@PathVariable String code) {
        return service.findById(code);
    }

    /** Создаёт новый сеанс и автоматически генерирует билеты на все места зала (HTTP 201 Created). */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmSession create(@Valid @RequestBody SessionCreateRequest request) {
        return service.create(
                request.hallCode(),
                request.filmCode(),
                request.startTime(),
                request.basePrice()
        );
    }

    /** Обновляет существующий сеанс по коду и возвращает изменённую версию (HTTP 200). */
    @PutMapping("/{code}")
    public FilmSession update(@PathVariable String code, @Valid @RequestBody SessionUpdateRequest request) {
        return service.update(code, request.filmCode(), request.basePrice());
    }

    /** Удаляет сеанс по коду вместе с билетами. Тело ответа пустое (HTTP 204 No Content). */
    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String code) {
        service.delete(code);
    }
}