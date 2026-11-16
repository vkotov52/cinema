package com.cinema.service;

import com.cinema.exception.BusinessRuleException;
import com.cinema.exception.DuplicateResourceException;
import com.cinema.exception.InvalidRequestException;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.Film;
import com.cinema.model.Genre;
import com.cinema.repository.FilmRepository;
import com.cinema.repository.FilmSessionRepository;
import com.cinema.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Реализация FilmService. Содержит бизнес-логику управления фильмами, валидацию и проверку связанных данных. */
@Service
@Transactional
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    /** Допустимые возрастные рейтинги фильмов. */
    private static final Set<String> RATINGS = Set.of("0+", "6+", "12+", "16+", "18+");

    private final FilmRepository films;
    private final GenreRepository genres;
    private final FilmSessionRepository sessions;

    /** Создаёт новый фильм после проверки уникальности кода и валидации полей. */
    @Override
    public Film create(String code, String title, int duration, String ageRating, Set<String> genreCodes) {
        if (films.existsById(code)) {
            throw new DuplicateResourceException(
                    "Фильм уже существует: " + code,
                    "Выберите другой код фильма или измените существующий через PUT /api/films/{code}."
            );
        }
        validate(duration, ageRating);
        return films.save(new Film(code, title, duration, ageRating, loadGenres(genreCodes)));
    }

    /** Возвращает список всех фильмов. */
    @Override
    @Transactional(readOnly = true)
    public List<Film> findAll() {
        return films.findAll();
    }

    /** Возвращает фильм по коду или выбрасывает ResourceNotFoundException, если фильм не найден. */
    @Override
    @Transactional(readOnly = true)
    public Film findById(String code) {
        return films.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Фильм не найден: " + code,
                        "Проверьте код фильма. Список фильмов: GET /api/films."
                ));
    }

    /** Возвращает фильмы, название которых содержит заданную подстроку (без учёта регистра). Пустая строка возвращает все фильмы. */
    @Override
    @Transactional(readOnly = true)
    public List<Film> searchByTitle(String fragment) {
        return films.findByTitleContainingIgnoreCase(fragment == null ? "" : fragment);
    }

    /** Возвращает все фильмы заданного жанра. DISTINCT исключает дубликаты из-за связи ManyToMany. */
    @Override
    @Transactional(readOnly = true)
    public List<Film> findByGenre(String genreCode) {
        return films.findDistinctByGenres_GenreCode(genreCode);
    }

    /** Возвращает фильмы, удовлетворяющие одновременно фильтру по названию и жанру. */
    @Override
    @Transactional(readOnly = true)
    public List<Film> searchByTitleAndGenre(String fragment, String genreCode) {
        return searchByTitle(fragment).stream()
                .filter(film -> film.hasGenre(genreCode))
                .toList();
    }

    /** Обновляет данные существующего фильма по его коду после валидации полей. */
    @Override
    public Film update(String code, String title, int duration, String ageRating, Set<String> genreCodes) {
        Film film = findById(code);
        validate(duration, ageRating);

        film.setTitle(title);
        film.setDurationMin(duration);
        film.setAgeRating(ageRating);
        film.setGenres(loadGenres(genreCodes));
        return films.save(film);
    }

    /** Удаляет фильм по коду. Запрещено, если у фильма есть связанные сеансы. */
    @Override
    public void delete(String code) {
        if (!films.existsById(code)) {
            throw new ResourceNotFoundException(
                    "Фильм не найден: " + code,
                    "Проверьте код фильма. Список фильмов: GET /api/films."
            );
        }
        if (sessions.existsByFilm_FilmCode(code)) {
            throw new BusinessRuleException(
                    "Нельзя удалить фильм с существующими сеансами",
                    "Сначала удалите сеансы с этим фильмом."
            );
        }
        films.deleteById(code);
    }

    /** Проверяет допустимость возрастного рейтинга и длительности фильма. */
    private void validate(int duration, String ageRating) {
        if (!RATINGS.contains(ageRating)) {
            throw new InvalidRequestException(
                    "Недопустимый возрастной рейтинг: " + ageRating,
                    "Допустимые значения: 0+, 6+, 12+, 16+, 18+."
            );
        }
        if (duration < 1) {
            throw new InvalidRequestException(
                    "Длительность должна быть положительной",
                    "Укажите длительность от 1 до 600."
            );
        }
    }

    /** Загружает жанры по кодам. Проверяет, что все коды существуют, и выбрасывает исключение при несовпадении. */
    private Set<Genre> loadGenres(Set<String> genreCodes) {
        if (genreCodes == null || genreCodes.isEmpty()) {
            throw new InvalidRequestException(
                    "Необходимо выбрать хотя бы один жанр",
                    "Передайте в genreCodes хотя бы один код существующего жанра (GET /api/genres)."
            );
        }
        Set<Genre> found = new HashSet<>(genres.findAllById(genreCodes));
        if (found.size() != genreCodes.size()) {
            throw new InvalidRequestException(
                    "Указаны несуществующие жанры",
                    "Проверьте коды жанров. Список жанров: GET /api/genres."
            );
        }
        return found;
    }
}