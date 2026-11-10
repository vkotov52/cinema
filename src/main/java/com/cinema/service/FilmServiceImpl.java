package com.cinema.service;

import com.cinema.model.Film;
import com.cinema.model.FilmSession;
import com.cinema.model.Genre;
import com.cinema.repository.EntityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class FilmServiceImpl implements FilmService {

    private static final Set<String> AGE_RATINGS = Set.of("0+", "6+", "12+", "16+", "18+");

    private final EntityRepository<Film, String> films;
    private final EntityRepository<Genre, String> genres;
    private final EntityRepository<FilmSession, String> sessions;

    public FilmServiceImpl(EntityRepository<Film, String> films, EntityRepository<Genre, String> genres,
                           EntityRepository<FilmSession, String> sessions) {
        this.films = films;
        this.genres = genres;
        this.sessions = sessions;
    }

    @Override
    public synchronized Film create(String filmCode, String title, int durationMin, String ageRating,
                                    Set<String> genreCodes) {
        if (!AGE_RATINGS.contains(ageRating)) {
            throw new IllegalArgumentException("Возрастной рейтинг должен быть одним из: 0+, 6+, 12+, 16+, 18+");
        }
        if (genreCodes.isEmpty()) {
            throw new IllegalArgumentException("У фильма должен быть хотя бы один жанр");
        }
        if (films.findByKey(filmCode).isPresent()) {
            throw new IllegalStateException("Фильм уже существует: " + filmCode);
        }
        Set<Genre> filmGenres = new HashSet<>();
        for (String genreCode : genreCodes) {
            filmGenres.add(genres.findByKey(genreCode)
                    .orElseThrow(() -> new IllegalArgumentException("Жанр не найден: " + genreCode)));
        }
        return films.save(new Film(filmCode, title, durationMin, ageRating, filmGenres));
    }

    @Override
    public List<Film> findAll() {
        return films.findAll();
    }

    @Override
    public List<Film> searchByTitle(String fragment) {
        String needle = fragment.trim().toLowerCase(Locale.ROOT);
        List<Film> result = new ArrayList<>();
        for (Film film : films.findAll()) {
            if (film.getTitle().toLowerCase(Locale.ROOT).contains(needle)) {
                result.add(film);
            }
        }
        return result;
    }

    @Override
    public List<Film> findByGenre(String genreCode) {
        List<Film> result = new ArrayList<>();
        for (Film film : films.findAll()) {
            if (film.hasGenre(genreCode)) {
                result.add(film);
            }
        }
        return result;
    }

    @Override
    public synchronized void delete(String filmCode) {
        if (films.findByKey(filmCode).isEmpty()) {
            throw new IllegalArgumentException("Фильм не найден: " + filmCode);
        }
        for (FilmSession session : sessions.findAll()) {
            if (session.getFilm().getFilmCode().equals(filmCode)) {
                throw new IllegalStateException("Нельзя удалить фильм " + filmCode + ": у него есть сеансы");
            }
        }
        films.deleteByKey(filmCode);
    }
}
