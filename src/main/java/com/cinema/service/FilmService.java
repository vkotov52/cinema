package com.cinema.service;

import com.cinema.model.Film;

import java.util.List;
import java.util.Set;

/** Сервис для управления фильмами. */
public interface FilmService {

    /** Создаёт новый фильм с заданными параметрами и привязывает его к существующим жанрам. */
    Film create(String code, String title, int duration, String ageRating, Set<String> genreCodes);

    /** Возвращает список всех фильмов. */
    List<Film> findAll();

    /** Возвращает фильм по его коду или выбрасывает NoSuchElementException. */
    Film findById(String code);

    /** Возвращает фильмы, название которых содержит заданную подстроку (без учёта регистра). */
    List<Film> searchByTitle(String fragment);

    /** Возвращает все фильмы заданного жанра по его коду. */
    List<Film> findByGenre(String genreCode);

    /** Возвращает фильмы, удовлетворяющие одновременно названию и жанру. */
    List<Film> searchByTitleAndGenre(String fragment, String genreCode);

    /** Обновляет данные существующего фильма по его коду. */
    Film update(String code, String title, int duration, String ageRating, Set<String> genreCodes);

    /** Удаляет фильм по коду. Запрещено, если у фильма есть связанные сеансы. */
    void delete(String code);
}