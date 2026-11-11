package com.cinema.repository;

import com.cinema.model.Film;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Репозиторий для работы с сущностью Film. */
public interface FilmRepository extends JpaRepository<Film, String> {

    /** Возвращает список фильмов, название которых содержит заданную подстроку (без учёта регистра). */
    List<Film> findByTitleContainingIgnoreCase(String title);

    /** Возвращает список фильмов заданного жанра. DISTINCT исключает дубликаты из-за связи ManyToMany. */
    List<Film> findDistinctByGenres_GenreCode(String genreCode);

    /** Проверяет, существует ли хотя бы один фильм с заданным жанром (используется для запрета удаления жанра). */
    boolean existsByGenres_GenreCode(String genreCode);
}