package com.cinema.controller;

import com.cinema.controller.dto.FilmCreateRequest;
import com.cinema.controller.dto.FilmUpdateRequest;
import com.cinema.model.Film;
import com.cinema.service.FilmService;
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

/** REST-контроллер для управления фильмами. Обрабатывает CRUD-операции и фильтрацию. */
@RestController
@RequestMapping("/api/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService service;

    /** Возвращает список фильмов с возможностью фильтрации по названию и/или жанру (HTTP 200). */
    @GetMapping
    public List<Film> findAll(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genreCode
    ) {
        if (title != null && genreCode != null) {
            return service.searchByTitleAndGenre(title, genreCode);
        }
        if (genreCode != null) {
            return service.findByGenre(genreCode);
        }
        if (title != null) {
            return service.searchByTitle(title);
        }
        return service.findAll();
    }

    /** Возвращает фильм по его коду или выбрасывает NotFoundException (HTTP 200 / 404). */
    @GetMapping("/{code}")
    public Film findById(@PathVariable String code) {
        return service.findById(code);
    }

    /** Создаёт новый фильм по данным из запроса и возвращает его (HTTP 201 Created). */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody FilmCreateRequest request) {
        return service.create(
                request.code(),
                request.title(),
                request.durationMin(),
                request.ageRating(),
                request.genreCodes()
        );
    }

    /** Обновляет существующий фильм по коду и возвращает изменённую версию (HTTP 200). */
    @PutMapping("/{code}")
    public Film update(@PathVariable String code, @Valid @RequestBody FilmUpdateRequest request) {
        return service.update(
                code,
                request.title(),
                request.durationMin(),
                request.ageRating(),
                request.genreCodes()
        );
    }

    /** Удаляет фильм по коду. Тело ответа пустое (HTTP 204 No Content). */
    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String code) {
        service.delete(code);
    }
}