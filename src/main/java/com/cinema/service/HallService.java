package com.cinema.service;

import com.cinema.model.Hall;

import java.util.List;

public interface HallService {

    Hall create(String hallCode, String name, int rowsCount, int seatsPerRow);

    List<Hall> findAll();

    /** Удаляет зал. Нельзя удалить зал, в котором есть сеансы. */
    void delete(String hallCode);
}
