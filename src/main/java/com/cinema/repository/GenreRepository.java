package com.cinema.repository;

import com.cinema.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

/** Репозиторий для работы с сущностью Genre. */
public interface GenreRepository extends JpaRepository<Genre, String> {
}