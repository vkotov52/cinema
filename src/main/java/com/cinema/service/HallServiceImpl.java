package com.cinema.service;

import com.cinema.model.FilmSession;
import com.cinema.model.Hall;
import com.cinema.repository.EntityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HallServiceImpl implements HallService {

    private final EntityRepository<Hall, String> halls;
    private final EntityRepository<FilmSession, String> sessions;

    public HallServiceImpl(EntityRepository<Hall, String> halls, EntityRepository<FilmSession, String> sessions) {
        this.halls = halls;
        this.sessions = sessions;
    }

    @Override
    public synchronized Hall create(String hallCode, String name, int rowsCount, int seatsPerRow) {
        if (halls.findByKey(hallCode).isPresent()) {
            throw new IllegalStateException("Зал уже существует: " + hallCode);
        }
        return halls.save(new Hall(hallCode, name, rowsCount, seatsPerRow));
    }

    @Override
    public List<Hall> findAll() {
        return halls.findAll();
    }

    @Override
    public synchronized void delete(String hallCode) {
        if (halls.findByKey(hallCode).isEmpty()) {
            throw new IllegalArgumentException("Зал не найден: " + hallCode);
        }
        for (FilmSession session : sessions.findAll()) {
            if (session.getHall().getHallCode().equals(hallCode)) {
                throw new IllegalStateException("Нельзя удалить зал " + hallCode + ": в нём есть сеансы");
            }
        }
        halls.deleteByKey(hallCode);
    }
}
