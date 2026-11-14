package com.cinema.service;

import com.cinema.model.Genre;
import com.cinema.repository.FilmRepository;
import com.cinema.repository.GenreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = GenreServiceImpl.class)
class GenreServiceImplTest {

    @MockitoBean
    private GenreRepository genres;

    @MockitoBean
    private FilmRepository films;

    @Autowired
    private GenreService service;

    /** Проверяет успешное создание жанра и корректное сохранение его кода и названия. */
    @Test
    void createSavesGenre() {
        when(genres.save(any(Genre.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Genre result = service.create("DRAMA", "Драма");

        assertEquals("DRAMA", result.getGenreCode());
        assertEquals("Драма", result.getName());
    }

    /** Проверяет отказ в создании жанра с дублирующимся кодом. */
    @Test
    void createRejectsDuplicateCode() {
        when(genres.existsById("DRAMA")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.create("DRAMA", "Драма"));
        verify(genres, never()).save(any());
    }

    /** Проверяет бизнес-правило: жанр нельзя удалить, если он используется хотя бы одним фильмом. */
    @Test
    void deleteRejectsGenreUsedByFilms() {
        Genre genre = new Genre("DRAMA", "Драма");
        when(genres.findById("DRAMA")).thenReturn(Optional.of(genre));
        when(films.existsByGenres_GenreCode("DRAMA")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.delete("DRAMA"));
        verify(genres, never()).delete(any());
    }
}