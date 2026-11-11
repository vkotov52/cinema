package com.cinema.repository;

import com.cinema.model.FilmSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Репозиторий для работы с сущностью FilmSession. */
public interface FilmSessionRepository extends JpaRepository<FilmSession, String> {

    /** Возвращает список всех сеансов, проходящих в зале с заданным кодом. */
    List<FilmSession> findByHall_HallCode(String hallCode);

    /** Проверяет, существует ли хотя бы один сеанс в зале с заданным кодом (используется для запрета удаления зала). */
    boolean existsByHall_HallCode(String hallCode);

    /** Проверяет, существует ли хотя бы один сеанс с заданным фильмом (используется для запрета удаления фильма). */
    boolean existsByFilm_FilmCode(String filmCode);
}