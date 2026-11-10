package com.cinema.service;

import com.cinema.TestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmServiceTest {

    private TestFixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new TestFixture();
        fixture.genreService.create("SCIFI", "Фантастика");
        fixture.genreService.create("DRAMA", "Драма");
        fixture.filmService.create("FLM-1", "Dune: Part Two", 166, "12+", Set.of("SCIFI"));
        fixture.filmService.create("FLM-2", "Titanic", 195, "12+", Set.of("DRAMA"));
    }

    @Test
    void create_shouldRejectUnknownGenre() {
        assertThrows(IllegalArgumentException.class,
                () -> fixture.filmService.create("FLM-3", "New", 100, "0+", Set.of("HORROR")));
    }

    @Test
    void searchByTitle_shouldIgnoreCase() {
        assertEquals(1, fixture.filmService.searchByTitle("DUNE").size());
        assertEquals("FLM-1", fixture.filmService.searchByTitle("DUNE").getFirst().getFilmCode());
    }

    @Test
    void findByGenre_shouldReturnFilmsOfGenre() {
        assertEquals(1, fixture.filmService.findByGenre("DRAMA").size());
        assertEquals("FLM-2", fixture.filmService.findByGenre("DRAMA").getFirst().getFilmCode());
    }

    @Test
    void delete_shouldRejectFilmWithSessions() {
        fixture.hallService.create("H1", "Hall 1", 1, 1);
        fixture.sessionService.create("H1", "FLM-1", TestFixture.EVENING, BigDecimal.TEN);

        assertThrows(IllegalStateException.class, () -> fixture.filmService.delete("FLM-1"));
    }
}
