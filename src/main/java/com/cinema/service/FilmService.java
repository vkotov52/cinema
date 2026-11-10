package com.cinema.service;

import com.cinema.model.Film;

import java.util.List;
import java.util.Set;

public interface FilmService {

    Film create(String filmCode, String title, int durationMin, String ageRating, Set<String> genreCodes);

    List<Film> findAll();

    List<Film> searchByTitle(String fragment);

    List<Film> findByGenre(String genreCode);

    void delete(String filmCode);
}
