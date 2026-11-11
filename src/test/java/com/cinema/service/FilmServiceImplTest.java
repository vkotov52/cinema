package com.cinema.service;

import com.cinema.model.Film;
import com.cinema.model.Genre;
import com.cinema.repository.FilmRepository;
import com.cinema.repository.FilmSessionRepository;
import com.cinema.repository.GenreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = FilmServiceImpl.class)
class FilmServiceImplTest {

    @MockitoBean
    private FilmRepository films;

    @MockitoBean
    private GenreRepository genres;

    @MockitoBean
    private FilmSessionRepository sessions;

    @Autowired
    private FilmService service;

    /** Тестовые данные жанров, переиспользуемые в различных сценариях проверки. */
    private final Genre drama = new Genre("DRAMA", "Драма");
    private final Genre comedy = new Genre("COMEDY", "Комедия");

    /** Проверяет успешное создание фильма, корректную привязку жанров и факт вызова метода сохранения. */
    @Test
    void createSavesFilm() {
        when(genres.findAllById(Set.of("DRAMA"))).thenReturn(List.of(drama));
        when(films.save(any(Film.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Film result = service.create("F001", "Тестовый фильм", 120, "16+", Set.of("DRAMA"));

        assertEquals("F001", result.getFilmCode());
        assertTrue(result.hasGenre("DRAMA"));
        verify(films).save(any(Film.class));
    }

    /** Проверяет отказ в создании фильма с дублирующимся кодом. */
    @Test
    void createRejectsDuplicateCode() {
        when(films.existsById("F001")).thenReturn(true);

        assertThrows(
                IllegalStateException.class,
                () -> service.create("F001", "Фильм", 120, "16+", Set.of("DRAMA"))
        );
        verify(films, never()).save(any());
    }

    /** Проверяет валидацию входных данных: попытка создать фильм без жанров должна приводить к IllegalArgumentException. */
    @Test
    void createRejectsEmptyGenres() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.create("F001", "Фильм", 120, "16+", Set.of())
        );
    }

    /** Проверяет валидацию входных данных: указание кода несуществующего жанра должно приводить к ошибке. */
    @Test
    void createRejectsUnknownGenre() {
        when(genres.findAllById(Set.of("DRAMA"))).thenReturn(List.of());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.create("F001", "Фильм", 120, "16+", Set.of("DRAMA"))
        );
    }

    /** Проверяет корректную обработку ситуации, когда запрашиваемый по коду фильм отсутствует в базе данных. */
    @Test
    void findByIdThrowsWhenFilmDoesNotExist() {
        when(films.findById("F001")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.findById("F001"));
    }

    /** Проверяет комбинированный поиск: результаты фильтрации по названию должны дополнительно отсеиваться по наличию заданного жанра. */
    @Test
    void searchByTitleAndGenreKeepsOnlyFilmsWithGenre() {
        Film dramaFilm = new Film("F1", "Тест драма", 100, "12+", Set.of(drama));
        Film comedyFilm = new Film("F2", "Тест комедия", 90, "12+", Set.of(comedy));
        when(films.findByTitleContainingIgnoreCase("тест")).thenReturn(List.of(dramaFilm, comedyFilm));

        List<Film> result = service.searchByTitleAndGenre("тест", "DRAMA");

        assertEquals(List.of(dramaFilm), result);
    }
}