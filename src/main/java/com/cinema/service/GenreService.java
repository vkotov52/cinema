package com.cinema.service;

import com.cinema.model.Genre;

import java.util.List;

/** Сервис для управления жанрами фильмов. */
public interface GenreService {

    /** Создаёт новый жанр с заданными кодом и названием. */
    Genre create(String code, String name);

    /** Возвращает список всех жанров. */
    List<Genre> findAll();

    /** Возвращает жанр по его коду или выбрасывает NoSuchElementException. */
    Genre findById(String code);

    /** Обновляет название существующего жанра по его коду. */
    Genre update(String code, String name);

    /** Удаляет жанр по коду. Запрещено, если жанр используется хотя бы одним фильмом. */
    void delete(String code);
}