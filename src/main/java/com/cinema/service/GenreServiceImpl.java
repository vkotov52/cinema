package com.cinema.service;

import com.cinema.model.Genre;
import com.cinema.repository.EntityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreServiceImpl implements GenreService {

    private final EntityRepository<Genre, String> genres;

    public GenreServiceImpl(EntityRepository<Genre, String> genres) {
        this.genres = genres;
    }

    @Override
    public synchronized Genre create(String genreCode, String name) {
        if (genres.findByKey(genreCode).isPresent()) {
            throw new IllegalStateException("Жанр уже существует: " + genreCode);
        }
        return genres.save(new Genre(genreCode, name));
    }

    @Override
    public List<Genre> findAll() {
        return genres.findAll();
    }
}
