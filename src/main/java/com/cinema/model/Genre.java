package com.cinema.model;

import java.util.Objects;

public class Genre {

    private final String genreCode;
    private final String name;

    public Genre(String genreCode, String name) {
        this.genreCode = genreCode;
        this.name = name;
    }

    public String getGenreCode() {
        return genreCode;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Genre && genreCode.equals(((Genre) other).genreCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genreCode);
    }
}


