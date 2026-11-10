package com.cinema.service;

import com.cinema.TestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

class GenreServiceTest {
    private TestFixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new TestFixture();
        fixture.genreService.create("ACTION", "Боевик");
        fixture.filmService.create("FLM-1", "Test Film", 100, "12+", Set.of("ACTION"));
    }

    @Test
    void delete_shouldRejectGenreUsedInFilm() {
        assertThrows(IllegalStateException.class, () -> fixture.genreService.delete("ACTION"));
    }

    @Test
    void delete_shouldRemoveUnusedGenre() {
        fixture.genreService.create("COMEDY", "Комедия");
        fixture.genreService.delete("COMEDY");
        assertThrows(IllegalArgumentException.class, () -> fixture.genreService.delete("COMEDY"));
    }
}