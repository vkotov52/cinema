package com.cinema.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** JPA-сущность жанра фильма. Таблица: genre. */
@Entity
@Table(name = "genre")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Genre {

    /** Первичный ключ — код жанра. Включен в генерацию equals() и hashCode() через Lombok. */
    @Id
    @Column(name = "genre_code", length = 20)
    @EqualsAndHashCode.Include
    @NotBlank
    private String genreCode;

    /** Название жанра. */
    @Setter
    @Column(nullable = false, unique = true, length = 50)
    @NotBlank
    private String name;

    /** Конструктор для создания нового жанра с заданным кодом и названием. */
    public Genre(String genreCode, String name) {
        this.genreCode = genreCode;
        this.name = name;
    }
}