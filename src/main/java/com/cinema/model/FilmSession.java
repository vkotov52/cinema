package com.cinema.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FilmSession {

    private static final DateTimeFormatter CODE_TIME = DateTimeFormatter.ofPattern("yyyyMMdd-HHmm");

    private final String sessionCode;
    private final Hall hall;
    private final Film film;
    private final LocalDateTime startTime;
    private final BigDecimal basePrice;

    public FilmSession(Hall hall, Film film, LocalDateTime startTime, BigDecimal basePrice) {
        if (basePrice.signum() < 0) {
            throw new IllegalArgumentException("Цена не может быть отрицательной");
        }
        this.hall = hall;
        this.film = film;
        this.startTime = startTime;
        this.basePrice = basePrice;
        this.sessionCode = hall.getHallCode() + "-" + startTime.format(CODE_TIME);
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public Hall getHall() {
        return hall;
    }

    public Film getFilm() {
        return film;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }
}
