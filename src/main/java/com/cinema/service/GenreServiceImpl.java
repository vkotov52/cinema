package com.cinema.service;

import com.cinema.exception.BusinessRuleException;
import com.cinema.exception.DuplicateResourceException;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.Genre;
import com.cinema.repository.FilmRepository;
import com.cinema.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Реализация GenreService. Содержит бизнес-логику управления жанрами и проверку связанных фильмов. */
@Service
@Transactional
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genres;
    private final FilmRepository films;

    /** Создаёт новый жанр после проверки уникальности кода. */
    @Override
    public Genre create(String code, String name) {
        if (genres.existsById(code)) {
            throw new DuplicateResourceException(
                    "Жанр уже существует: " + code,
                    "Выберите другой код жанра или измените существующий через PUT /api/genres/{code}."
            );
        }
        return genres.save(new Genre(code, name));
    }

    /** Возвращает список всех жанров. */
    @Override
    @Transactional(readOnly = true)
    public List<Genre> findAll() {
        return genres.findAll();
    }

    /** Возвращает жанр по коду или выбрасывает ResourceNotFoundException, если жанр не найден. */
    @Override
    @Transactional(readOnly = true)
    public Genre findById(String code) {
        return genres.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Жанр не найден: " + code,
                        "Проверьте код жанра. Список жанров: GET /api/genres."
                ));
    }

    /** Обновляет название существующего жанра по его коду. */
    @Override
    public Genre update(String code, String name) {
        Genre genre = findById(code);
        genre.setName(name);
        return genres.save(genre);
    }

    /** Удаляет жанр по коду. Запрещено, если жанр используется хотя бы одним фильмом. */
    @Override
    public void delete(String code) {
        Genre genre = findById(code);
        if (films.existsByGenres_GenreCode(code)) {
            throw new BusinessRuleException(
                    "Жанр используется в фильмах",
                    "Уберите жанр из фильмов (PUT /api/films/{code}) и повторите удаление."
            );
        }
        genres.delete(genre);
    }
}