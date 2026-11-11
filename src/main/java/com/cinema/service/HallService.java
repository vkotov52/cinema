package com.cinema.service;

import com.cinema.model.Hall;

import java.util.List;

/** Сервис для управления кинозалами. */
public interface HallService {

    /** Создаёт новый зал с заданными параметрами: код, название, количество рядов и мест в ряду. */
    Hall create(String code, String name, int rows, int seats);

    /** Возвращает список всех залов. */
    List<Hall> findAll();

    /** Возвращает зал по его коду или выбрасывает NoSuchElementException. */
    Hall findById(String code);

    /** Обновляет параметры существующего зала по его коду. */
    Hall update(String code, String name, int rows, int seats);

    /** Удаляет зал по коду. Запрещено, если в зале есть хотя бы один сеанс. */
    void delete(String code);
}