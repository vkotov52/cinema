package com.cinema.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** JPA-сущность кинозала. Таблица: hall. */
@Entity
@Table(name = "hall")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hall {

    /** Первичный ключ — уникальный код зала (например, IMAX или HALL_1). */
    @Id
    @Column(name = "hall_code", length = 10)
    @NotBlank
    private String hallCode;

    /** Название зала. */
    @Setter
    @Column(nullable = false, unique = true, length = 50)
    @NotBlank
    private String name;

    /** Количество рядов в зале (от 1 до 50). */
    @Setter
    @Column(name = "rows_count", nullable = false)
    @Min(1)
    @Max(50)
    private int rowsCount;

    /** Количество мест в одном ряду (от 1 до 60). */
    @Setter
    @Column(name = "seats_per_row", nullable = false)
    @Min(1)
    @Max(60)
    private int seatsPerRow;

    /** Конструктор для создания нового зала с заданными параметрами. */
    public Hall(String hallCode, String name, int rowsCount, int seatsPerRow) {
        this.hallCode = hallCode;
        this.name = name;
        this.rowsCount = rowsCount;
        this.seatsPerRow = seatsPerRow;
    }

    /** Вычисляет и возвращает общую вместимость зала (количество рядов умноженное на количество мест). */
    public int capacity() {
        return rowsCount * seatsPerRow;
    }
}