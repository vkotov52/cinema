package com.cinema.service;

import com.cinema.model.FilmSession;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Сервис для управления сеансами фильмов. */
public interface SessionService {

    /** Создаёт новый сеанс в заданном зале с указанным фильмом, временем и базовой ценой. */
    FilmSession create(String hallCode, String filmCode, LocalDateTime time, BigDecimal price);

    /** Возвращает список всех сеансов. */
    List<FilmSession> findAll();

    /** Возвращает сеанс по его коду или выбрасывает NoSuchElementException. */
    FilmSession findById(String code);

    /** Возвращает список всех сеансов, проходящих в зале с заданным кодом. */
    List<FilmSession> findByHall(String hallCode);

    /** Обновляет фильм и базовую цену существующего сеанса по его коду. */
    FilmSession update(String code, String filmCode, BigDecimal price);

    /** Удаляет сеанс по коду вместе со всеми связанными билетами. */
    void delete(String code);
}