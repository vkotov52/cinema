package com.cinema.service;

import com.cinema.model.Genre;

import java.util.List;

public interface GenreService {

    Genre create(String genreCode, String name);

    List<Genre> findAll();
}
