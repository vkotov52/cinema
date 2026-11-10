package com.cinema.service;

import com.cinema.model.Film;
import com.cinema.model.FilmSession;
import com.cinema.model.Hall;
import com.cinema.model.Ticket;
import com.cinema.model.TicketStatus;
import com.cinema.repository.EntityRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SessionServiceImpl implements SessionService {

    private final EntityRepository<Hall, String> halls;
    private final EntityRepository<Film, String> films;
    private final EntityRepository<FilmSession, String> sessions;
    private final EntityRepository<Ticket, String> tickets;
    private final AtomicInteger ticketCounter = new AtomicInteger();

    public SessionServiceImpl(EntityRepository<Hall, String> halls, EntityRepository<Film, String> films,
                              EntityRepository<FilmSession, String> sessions,
                              EntityRepository<Ticket, String> tickets) {
        this.halls = halls;
        this.films = films;
        this.sessions = sessions;
        this.tickets = tickets;
    }

    @Override
    public synchronized FilmSession create(String hallCode, String filmCode, LocalDateTime startTime,
                                           BigDecimal basePrice) {
        Hall hall = halls.findByKey(hallCode)
                .orElseThrow(() -> new IllegalArgumentException("Зал не найден: " + hallCode));
        Film film = films.findByKey(filmCode)
                .orElseThrow(() -> new IllegalArgumentException("Фильм не найден: " + filmCode));

        FilmSession session = new FilmSession(hall, film, startTime, basePrice);
        if (sessions.findByKey(session.getSessionCode()).isPresent()) {
            throw new IllegalStateException("В зале " + hallCode + " в это время уже есть сеанс");
        }
        sessions.save(session);

        for (int row = 1; row <= hall.getRowsCount(); row++) {
            for (int seat = 1; seat <= hall.getSeatsPerRow(); seat++) {
                String number = String.format("TKT-%06d", ticketCounter.incrementAndGet());
                tickets.save(new Ticket(number, session, row, seat, basePrice));
            }
        }
        return session;
    }

    @Override
    public List<FilmSession> findAll() {
        return sessions.findAll();
    }

    @Override
    public List<FilmSession> findByHall(String hallCode) {
        List<FilmSession> result = new ArrayList<>();
        for (FilmSession session : sessions.findAll()) {
            if (session.getHall().getHallCode().equals(hallCode)) {
                result.add(session);
            }
        }
        return result;
    }

    @Override
    public synchronized void delete(String sessionCode) {
        if (sessions.findByKey(sessionCode).isEmpty()) {
            throw new IllegalArgumentException("Сеанс не найден: " + sessionCode);
        }
        List<Ticket> sessionTickets = new ArrayList<>();
        for (Ticket ticket : tickets.findAll()) {
            if (ticket.getSessionCode().equals(sessionCode)) {
                if (ticket.getStatus() == TicketStatus.SOLD) {
                    throw new IllegalStateException("Нельзя удалить сеанс " + sessionCode + ": есть проданные билеты");
                }
                sessionTickets.add(ticket);
            }
        }
        for (Ticket ticket : sessionTickets) {
            tickets.deleteByKey(ticket.getTicketNumber());
        }
        sessions.deleteByKey(sessionCode);
    }
}
