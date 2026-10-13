package com.cinema.service;

import com.cinema.model.Film;
import com.cinema.model.FilmSession;
import com.cinema.model.Hall;
import com.cinema.model.Ticket;
import com.cinema.model.TicketStatus;
import com.cinema.repository.EntityRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    private final EntityRepository<Hall, String> halls;
    private final EntityRepository<Film, String> films;
    private final EntityRepository<FilmSession, String> sessions;
    private final EntityRepository<Ticket, String> tickets;

    public ReportServiceImpl(EntityRepository<Hall, String> halls, EntityRepository<Film, String> films,
                             EntityRepository<FilmSession, String> sessions,
                             EntityRepository<Ticket, String> tickets) {
        this.halls = halls;
        this.films = films;
        this.sessions = sessions;
        this.tickets = tickets;
    }

    @Override
    public BigDecimal sessionRevenue(String sessionCode) {
        if (sessions.findByKey(sessionCode).isEmpty()) {
            throw new IllegalArgumentException("Сеанс не найден: " + sessionCode);
        }
        BigDecimal total = BigDecimal.ZERO;
        for (Ticket ticket : tickets.findAll()) {
            if (ticket.getSessionCode().equals(sessionCode) && ticket.getStatus() == TicketStatus.SOLD) {
                total = total.add(ticket.getPrice());
            }
        }
        return total;
    }

    @Override
    public Map<String, Long> soldTicketsByFilm() {
        Map<String, Long> counts = new HashMap<>();
        for (Film film : films.findAll()) {
            counts.put(film.getTitle(), 0L);
        }
        for (Ticket ticket : tickets.findAll()) {
            if (ticket.getStatus() == TicketStatus.SOLD) {
                counts.merge(ticket.getSession().getFilm().getTitle(), 1L, Long::sum);
            }
        }
        List<Map.Entry<String, Long>> entries = new ArrayList<>(counts.entrySet());
        entries.sort(Map.Entry.<String, Long>comparingByValue().reversed());
        Map<String, Long> sorted = new LinkedHashMap<>();
        for (Map.Entry<String, Long> entry : entries) {
            sorted.put(entry.getKey(), entry.getValue());
        }
        return sorted;
    }

    @Override
    public Statistics statistics() {
        long sold = 0;
        for (Ticket ticket : tickets.findAll()) {
            if (ticket.getStatus() == TicketStatus.SOLD) {
                sold++;
            }
        }
        return new Statistics(halls.findAll().size(), films.findAll().size(), sessions.findAll().size(), sold);
    }
}
