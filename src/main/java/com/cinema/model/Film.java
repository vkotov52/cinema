package com.cinema.model;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class Film {

    private final String filmCode;
    private final String title;
    private final int durationMin;
    private final String ageRating;
    private final Set<Genre> genres = new CopyOnWriteArraySet<>();

    public Film(String filmCode, String title, int durationMin, String ageRating, Set<Genre> genres) {
        if (durationMin <= 0) {
            throw new IllegalArgumentException("Длительность должна быть больше нуля");
        }
        this.filmCode = filmCode;
        this.title = title;
        this.durationMin = durationMin;
        this.ageRating = ageRating;
        this.genres.addAll(genres);
    }

    public String getFilmCode() {
        return filmCode;
    }

    public String getTitle() {
        return title;
    }

    public int getDurationMin() {
        return durationMin;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public Set<Genre> getGenres() {
        return Collections.unmodifiableSet(genres);
    }

    public boolean hasGenre(String genreCode) {
        for (Genre genre : genres) {
            if (genre.getGenreCode().equals(genreCode)) {
                return true;
            }
        }
        return false;
    }
}
