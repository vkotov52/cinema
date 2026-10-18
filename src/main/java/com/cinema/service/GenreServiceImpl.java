package com.cinema.service;

import com.cinema.model.Film;
import com.cinema.model.Genre;
import com.cinema.repository.EntityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreServiceImpl implements GenreService {

    private final EntityRepository<Genre, String> genres;
    private final EntityRepository<Film, String> films;

    public GenreServiceImpl(EntityRepository<Genre, String> genres, EntityRepository<Film, String> films) {
        this.genres = genres;
        this.films = films;
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

    @Override
    public synchronized void delete(String genreCode) {
        if (genres.findByKey(genreCode).isEmpty()) {
            throw new IllegalArgumentException("Жанр не найден: " + genreCode);
        }
        for (Film film : films.findAll()) {
            if (film.hasGenre(genreCode)) {
                throw new IllegalStateException("Нельзя удалить жанр " + genreCode + ": он используется в фильме " + film.getFilmCode());
            }
        }
        genres.deleteByKey(genreCode);
    }
}