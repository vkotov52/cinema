package com.cinema.repository;

import com.cinema.model.Hall;
import org.springframework.data.jpa.repository.JpaRepository;

/** Репозиторий для работы с сущностью Hall. */
public interface HallRepository extends JpaRepository<Hall, String> {
}