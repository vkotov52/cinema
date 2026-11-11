package com.cinema.service;

import com.cinema.model.CustomerUser;
import com.cinema.model.Film;
import com.cinema.model.FilmSession;
import com.cinema.model.Genre;
import com.cinema.model.Hall;
import com.cinema.model.Ticket;
import com.cinema.model.TicketStatus;
import com.cinema.repository.FilmRepository;
import com.cinema.repository.FilmSessionRepository;
import com.cinema.repository.HallRepository;
import com.cinema.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = SessionServiceImpl.class)
class SessionServiceImplTest {

    @MockitoBean
    private HallRepository halls;

    @MockitoBean
    private FilmRepository films;

    @MockitoBean
    private FilmSessionRepository sessions;

    @MockitoBean
    private TicketRepository tickets;

    @Autowired
    private SessionService service;

    /** Тестовые данные: время начала сеанса, зал 2×3, два фильма и жанр для проверки бизнес-логики. */
    private final LocalDateTime start = LocalDateTime.of(2026, 11, 20, 12, 0);
    private final Hall hall = new Hall("H1", "Зал 1", 2, 3);
    private final Film film = new Film("F1", "Фильм", 120, "12+", Set.of(new Genre("DRAMA", "Драма")));
    private final Film otherFilm = new Film("F2", "Фильм 2", 90, "12+", Set.of(new Genre("DRAMA", "Драма")));

    /** Подготавливает данные: зал и фильм считаются уже существующими в базе. */
    private void givenHallAndFilmExist() {
        when(halls.findById("H1")).thenReturn(Optional.of(hall));
        when(films.findById("F1")).thenReturn(Optional.of(film));
    }

    /** Проверяет, что при создании сеанса автоматически генерируется ровно один билет на каждое место зала (2×3=6), все со статусом AVAILABLE. */
    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void createGeneratesTicketForEverySeat() {
        givenHallAndFilmExist();

        FilmSession result = service.create("H1", "F1", start.plusSeconds(30), BigDecimal.TEN);

        assertEquals("H1-20261120-1200", result.getSessionCode());
        assertEquals(start, result.getStartTime());

        ArgumentCaptor<List<Ticket>> captor = ArgumentCaptor.forClass((Class) List.class);
        verify(tickets).saveAll(captor.capture());
        assertEquals(6, captor.getValue().size());
        assertTrue(captor.getValue().stream().allMatch(t -> t.getStatus() == TicketStatus.AVAILABLE));
    }

    /** Проверяет, что создание сеанса отклоняется, если его временной интервал пересекается с уже существующим сеансом в том же зале. */
    @Test
    void createRejectsSessionOverlappingExistingOne() {
        givenHallAndFilmExist();
        FilmSession existing = new FilmSession(hall, film, start, BigDecimal.TEN);
        when(sessions.findByHall_HallCode("H1")).thenReturn(List.of(existing));

        assertThrows(
                IllegalStateException.class,
                () -> service.create("H1", "F1", start.plusMinutes(90), BigDecimal.TEN)
        );
        verify(sessions, never()).save(any());
    }

    /** Проверяет граничный случай: новый сеанс разрешён, если он начинается ровно в момент окончания предыдущего (без пересечения). */
    @Test
    void createAllowsSessionStartingWhenPreviousEnds() {
        givenHallAndFilmExist();
        FilmSession existing = new FilmSession(hall, film, start, BigDecimal.TEN);
        when(sessions.findByHall_HallCode("H1")).thenReturn(List.of(existing));

        FilmSession result = service.create("H1", "F1", start.plusMinutes(120), BigDecimal.TEN);

        assertEquals("H1-20261120-1400", result.getSessionCode());
    }

    /** Проверяет валидацию входных данных: попытка создать сеанс с отрицательной ценой должна приводить к IllegalArgumentException. */
    @Test
    void createRejectsNegativePrice() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.create("H1", "F1", start, new BigDecimal("-1.00"))
        );
        verify(sessions, never()).save(any());
    }

    /** Проверяет бизнес-правило: нельзя сменить фильм в сеансе, если по нему уже есть проданные билеты. */
    @Test
    void updateRejectsFilmChangeWhenTicketsAreSold() {
        FilmSession session = new FilmSession(hall, film, start, BigDecimal.TEN);
        Ticket sold = new Ticket("TKT-000001", session, 1, 1, BigDecimal.TEN);
        sold.trySell(LocalDateTime.now(), new CustomerUser("u", "p", "Иванов", "Иван", "i@i.ru"));

        when(sessions.findById(session.getSessionCode())).thenReturn(Optional.of(session));
        when(films.findById("F2")).thenReturn(Optional.of(otherFilm));
        when(tickets.findBySession_SessionCode(session.getSessionCode())).thenReturn(List.of(sold));

        assertThrows(
                IllegalStateException.class,
                () -> service.update(session.getSessionCode(), "F2", BigDecimal.TEN)
        );
    }

    /** Проверяет, что при изменении базовой цены обновляются только свободные билеты, а цены проданных остаются неизменными. */
    @Test
    void updateChangesPriceOnlyOfAvailableTickets() {
        FilmSession session = new FilmSession(hall, film, start, BigDecimal.TEN);
        Ticket available = new Ticket("TKT-000001", session, 1, 1, BigDecimal.TEN);
        Ticket sold = new Ticket("TKT-000002", session, 1, 2, BigDecimal.TEN);
        sold.trySell(LocalDateTime.now(), new CustomerUser("u", "p", "Иванов", "Иван", "i@i.ru"));
        BigDecimal newPrice = new BigDecimal("500.00");

        when(sessions.findById(session.getSessionCode())).thenReturn(Optional.of(session));
        when(sessions.findByHall_HallCode("H1")).thenReturn(List.of(session));
        when(sessions.save(any(FilmSession.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(films.findById("F1")).thenReturn(Optional.of(film));
        when(tickets.findBySession_SessionCode(session.getSessionCode()))
                .thenReturn(List.of(available, sold));

        FilmSession result = service.update(session.getSessionCode(), "F1", newPrice);

        assertEquals(newPrice, result.getBasePrice());
        assertEquals(newPrice, available.getPrice());
        assertEquals(BigDecimal.TEN, sold.getPrice());
    }
}