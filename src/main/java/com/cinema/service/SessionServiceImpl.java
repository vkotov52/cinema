package com.cinema.service;

import com.cinema.model.Film;
import com.cinema.model.FilmSession;
import com.cinema.model.Hall;
import com.cinema.model.Ticket;
import com.cinema.model.TicketStatus;
import com.cinema.repository.FilmRepository;
import com.cinema.repository.FilmSessionRepository;
import com.cinema.repository.HallRepository;
import com.cinema.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/** Реализация SessionService. Содержит бизнес-логику управления сеансами, проверку пересечений и автоматическую генерацию билетов. */
@Service
@Transactional
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final HallRepository halls;
    private final FilmRepository films;
    private final FilmSessionRepository sessions;
    private final TicketRepository tickets;

    /** Создаёт новый сеанс после валидации цены и проверки отсутствия пересечений с другими сеансами в зале. Автоматически генерирует билеты для всех мест зала. */
    @Override
    public FilmSession create(String hallCode, String filmCode, LocalDateTime time, BigDecimal price) {
        validatePrice(price);
        if (time == null) {
            throw new IllegalArgumentException("Время начала обязательно");
        }

        Hall hall = findHall(hallCode);
        Film film = findFilm(filmCode);
        LocalDateTime start = time.truncatedTo(ChronoUnit.MINUTES);

        checkNoOverlap(hallCode, start, film, null);

        FilmSession session = new FilmSession(hall, film, start, price);
        sessions.save(session);
        tickets.saveAll(generateTickets(session, price));
        return session;
    }

    /** Возвращает список всех сеансов. */
    @Override
    @Transactional(readOnly = true)
    public List<FilmSession> findAll() {
        return sessions.findAll();
    }

    /** Возвращает сеанс по коду или выбрасывает NoSuchElementException, если сеанс не найден. */
    @Override
    @Transactional(readOnly = true)
    public FilmSession findById(String code) {
        return sessions.findById(code)
                .orElseThrow(() -> new NoSuchElementException("Сеанс не найден: " + code));
    }

    /** Возвращает список всех сеансов, проходящих в зале с заданным кодом. */
    @Override
    @Transactional(readOnly = true)
    public List<FilmSession> findByHall(String hallCode) {
        return sessions.findByHall_HallCode(hallCode);
    }

    /** Обновляет фильм и цену сеанса. Запрещает смену фильма при наличии проданных билетов. Обновляет цены только для свободных билетов. */
    @Override
    public FilmSession update(String code, String filmCode, BigDecimal price) {
        FilmSession session = findById(code);
        validatePrice(price);
        Film film = findFilm(filmCode);

        List<Ticket> sessionTickets = tickets.findBySession_SessionCode(code);

        boolean filmChanged = !session.getFilm().getFilmCode().equals(filmCode);
        if (filmChanged && hasSoldTickets(sessionTickets)) {
            throw new IllegalStateException("Нельзя сменить фильм в сеансе с проданными билетами");
        }

        checkNoOverlap(session.getHall().getHallCode(), session.getStartTime(), film, code);

        session.setFilm(film);
        session.setBasePrice(price);
        sessionTickets.stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.AVAILABLE)
                .forEach(ticket -> ticket.setPrice(price));

        tickets.saveAll(sessionTickets);
        return sessions.save(session);
    }

    /** Удаляет сеанс по коду вместе со всеми связанными билетами. Запрещено, если есть проданные билеты. */
    @Override
    public void delete(String code) {
        FilmSession session = findById(code);
        List<Ticket> sessionTickets = tickets.findBySession_SessionCode(code);

        if (hasSoldTickets(sessionTickets)) {
            throw new IllegalStateException("Нельзя удалить сеанс с проданными билетами");
        }

        tickets.deleteAll(sessionTickets);
        sessions.delete(session);
    }

    /** Находит зал по коду или выбрасывает NoSuchElementException. */
    private Hall findHall(String code) {
        return halls.findById(code)
                .orElseThrow(() -> new NoSuchElementException("Зал не найден: " + code));
    }

    /** Находит фильм по коду или выбрасывает NoSuchElementException. */
    private Film findFilm(String code) {
        return films.findById(code)
                .orElseThrow(() -> new NoSuchElementException("Фильм не найден: " + code));
    }

    /** Проверяет, что цена не null и не отрицательная. */
    private void validatePrice(BigDecimal price) {
        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("Цена не может быть отрицательной");
        }
    }

    /** Проверяет, есть ли среди билетов хотя бы один со статусом SOLD. */
    private boolean hasSoldTickets(List<Ticket> sessionTickets) {
        return sessionTickets.stream().anyMatch(ticket -> ticket.getStatus() == TicketStatus.SOLD);
    }

    /** Проверяет, что новый сеанс не пересекается по времени с другими сеансами в том же зале. */
    private void checkNoOverlap(String hallCode, LocalDateTime start, Film film, String ignoredSessionCode) {
        LocalDateTime end = start.plusMinutes(film.getDurationMin());

        boolean overlaps = sessions.findByHall_HallCode(hallCode).stream()
                .filter(other -> !other.getSessionCode().equals(ignoredSessionCode))
                .anyMatch(other -> other.overlaps(start, end));

        if (overlaps) {
            throw new IllegalStateException("В этом зале на выбранное время уже есть другой сеанс");
        }
    }

    /** Генерирует билеты для всех мест зала (ряды × места в ряду) с уникальными номерами. */
    private List<Ticket> generateTickets(FilmSession session, BigDecimal price) {
        Hall hall = session.getHall();
        List<Ticket> generated = new ArrayList<>(hall.capacity());
        Set<String> usedNumbers = new HashSet<>();

        for (int row = 1; row <= hall.getRowsCount(); row++) {
            for (int seat = 1; seat <= hall.getSeatsPerRow(); seat++) {
                generated.add(new Ticket(generateTicketNumber(usedNumbers), session, row, seat, price));
            }
        }
        return generated;
    }

    /** Генерирует уникальный номер билета в формате TKT-XXXXXX, проверяя отсутствие дубликатов в БД и среди уже сгенерированных. */
    private String generateTicketNumber(Set<String> usedNumbers) {
        String number;
        do {
            number = "TKT-%06d".formatted(ThreadLocalRandom.current().nextInt(1_000_000));
        } while (tickets.existsById(number) || !usedNumbers.add(number));
        return number;
    }
}