package com.cinema.service;

import com.cinema.model.Hall;
import com.cinema.repository.FilmSessionRepository;
import com.cinema.repository.HallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/** Реализация HallService. Содержит бизнес-логику управления залами и проверку связанных сеансов. */
@Service
@Transactional
@RequiredArgsConstructor
public class HallServiceImpl implements HallService {

    private final HallRepository halls;
    private final FilmSessionRepository sessions;

    /** Создаёт новый зал после проверки уникальности кода. */
    @Override
    public Hall create(String code, String name, int rows, int seats) {
        if (halls.existsById(code)) {
            throw new IllegalStateException("Зал уже существует: " + code);
        }
        return halls.save(new Hall(code, name, rows, seats));
    }

    /** Возвращает список всех залов. */
    @Override
    @Transactional(readOnly = true)
    public List<Hall> findAll() {
        return halls.findAll();
    }

    /** Возвращает зал по коду или выбрасывает NoSuchElementException, если зал не найден. */
    @Override
    @Transactional(readOnly = true)
    public Hall findById(String code) {
        return halls.findById(code)
                .orElseThrow(() -> new NoSuchElementException("Зал не найден: " + code));
    }

    /** Обновляет параметры зала. Запрещает изменение схемы мест, если для зала существуют сеансы. */
    @Override
    public Hall update(String code, String name, int rows, int seats) {
        Hall hall = findById(code);

        boolean layoutChanged = hall.getRowsCount() != rows || hall.getSeatsPerRow() != seats;
        if (layoutChanged && sessions.existsByHall_HallCode(code)) {
            throw new IllegalStateException(
                    "Нельзя изменить схему мест зала, пока для него существуют сеансы"
            );
        }

        hall.setName(name);
        hall.setRowsCount(rows);
        hall.setSeatsPerRow(seats);
        return halls.save(hall);
    }

    /** Удаляет зал по коду. Запрещено, если в зале есть хотя бы один сеанс. */
    @Override
    public void delete(String code) {
        Hall hall = findById(code);
        if (sessions.existsByHall_HallCode(code)) {
            throw new IllegalStateException("Нельзя удалить зал с существующими сеансами");
        }
        halls.delete(hall);
    }
}