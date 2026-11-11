package com.cinema.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** JPA-сущность сеанса фильма. Таблица: film_session. Уникальное ограничение: один зал не может иметь два сеанса в одно время. */
@Entity
@Table(
        name = "film_session",
        uniqueConstraints = @UniqueConstraint(columnNames = {"hall_code", "start_time"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FilmSession {

    /** Формат для генерации кода сеанса из кода зала и времени начала (например, IMAX-20261028-1800). */
    private static final DateTimeFormatter CODE_TIME = DateTimeFormatter.ofPattern("yyyyMMdd-HHmm");

    /** Первичный ключ — код сеанса, формируется автоматически из кода зала и времени начала. */
    @Id
    @Column(name = "session_code", length = 30)
    @NotBlank
    private String sessionCode;

    /** Зал, в котором проходит сеанс. */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "hall_code", nullable = false)
    private Hall hall;

    /** Показываемый фильм. Можно изменить при обновлении сеанса (например, заменить фильм). */
    @Setter
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "film_code", nullable = false)
    private Film film;

    /** Дата и время начала сеанса. */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /** Базовая цена билета на сеанс. */
    @Setter
    @Column(name = "base_price", nullable = false, precision = 8, scale = 2)
    @DecimalMin("0.00")
    private BigDecimal basePrice;

    /** Конструктор для создания нового сеанса. Код генерируется автоматически из зала и времени. */
    public FilmSession(Hall hall, Film film, LocalDateTime startTime, BigDecimal basePrice) {
        this.hall = hall;
        this.film = film;
        this.startTime = startTime;
        this.basePrice = basePrice;
        this.sessionCode = hall.getHallCode() + "-" + startTime.format(CODE_TIME);
    }

    /** Возвращает время окончания сеанса, вычисленное как время начала плюс длительность фильма. */
    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(film.getDurationMin());
    }

    /** Проверяет, пересекается ли этот сеанс с другим по времени (используется для валидации расписания). */
    public boolean overlaps(LocalDateTime otherStart, LocalDateTime otherEnd) {
        return startTime.isBefore(otherEnd) && otherStart.isBefore(getEndTime());
    }
}