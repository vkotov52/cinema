package com.cinema.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/** JPA-сущность фильма. Таблица: film. Связь с Genre через таблицу film_genre. */
@Entity
@Table(name = "film")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Film {

    /** Первичный ключ — код фильма (формат FLM-0001). */
    @Id
    @Column(name = "film_code", length = 20)
    @NotBlank
    private String filmCode;

    /** Название фильма, до 200 символов. */
    @Setter
    @Column(nullable = false, length = 200)
    @NotBlank
    private String title;

    /** Длительность в минутах, от 1 до 600. */
    @Setter
    @Column(name = "duration_min", nullable = false)
    @Min(1)
    @Max(600)
    private int durationMin;

    /** Возрастной рейтинг: 0+, 6+, 12+, 16+, 18+. */
    @Setter
    @Column(name = "age_rating", nullable = false, length = 3)
    @NotBlank
    private String ageRating;

    /** Жанры фильма. */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "film_genre",
            joinColumns = @JoinColumn(name = "film_code"),
            inverseJoinColumns = @JoinColumn(name = "genre_code")
    )
    private Set<Genre> genres = new HashSet<>();

    /** Конструктор для создания нового фильма с заданными жанрами. */
    public Film(String filmCode, String title, int durationMin, String ageRating, Set<Genre> genres) {
        this.filmCode = filmCode;
        this.title = title;
        this.durationMin = durationMin;
        this.ageRating = ageRating;
        setGenres(genres);
    }

    /** Устанавливает жанры через копирование, чтобы избежать утечки внешней ссылки. */
    public void setGenres(Set<Genre> newGenres) {
        Set<Genre> copy = newGenres == null ? new HashSet<>() : new HashSet<>(newGenres);
        this.genres.clear();
        this.genres.addAll(copy);
    }

    /** Проверяет, принадлежит ли фильм заданному жанру по его коду. */
    public boolean hasGenre(String genreCode) {
        return genres.stream().anyMatch(genre -> genre.getGenreCode().equals(genreCode));
    }
}