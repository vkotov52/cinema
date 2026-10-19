package com.cinema.service;

import com.cinema.model.FilmSession;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface SessionService {

    /** Создаёт сеанс и билет на каждое место зала. */
    FilmSession create(String hallCode, String filmCode, LocalDateTime startTime, BigDecimal basePrice);

    List<FilmSession> findAll();

    List<FilmSession> findByHall(String hallCode);

    /** Удаляет сеанс вместе с билетами. Нельзя удалить сеанс с проданными билетами. */
    void delete(String sessionCode);
}
