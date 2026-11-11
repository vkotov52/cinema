package com.cinema.service;

import com.cinema.model.CustomerUser;
import com.cinema.model.Film;
import com.cinema.model.FilmSession;
import com.cinema.model.Genre;
import com.cinema.model.Hall;
import com.cinema.model.Ticket;
import com.cinema.model.TicketStatus;
import com.cinema.model.User;
import com.cinema.repository.TicketRepository;
import com.cinema.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TicketServiceImpl.class)
class TicketServiceImplTest {

    @MockitoBean
    private TicketRepository tickets;

    @MockitoBean
    private UserRepository users;

    @Autowired
    private TicketService service;

    /** Тестовые данные покупателя, переиспользуемые в сценариях проверки. */
    private final User customer = new CustomerUser("customer", "p", "Иванов", "Иван", "i@i.ru");

    /** Создаёт тестовый билет на сеанс с одним местом для использования в сценариях. */
    private Ticket newTicket() {
        Film film = new Film("F1", "Фильм", 90, "12+", Set.of(new Genre("DRAMA", "Драма")));
        FilmSession session = new FilmSession(
                new Hall("H1", "Зал", 1, 1),
                film,
                LocalDateTime.of(2026, 11, 20, 12, 0),
                BigDecimal.TEN
        );
        return new Ticket("TKT-000001", session, 1, 1, BigDecimal.TEN);
    }

    /** Проверяет успешную покупку билета: смену статуса на SOLD и привязку к покупателю. */
    @Test
    void buySellsAvailableTicket() {
        Ticket ticket = newTicket();
        when(users.findById("customer")).thenReturn(Optional.of(customer));
        when(tickets.findById("TKT-000001")).thenReturn(Optional.of(ticket));
        when(tickets.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ticket result = service.buy("TKT-000001", "customer");

        assertEquals(TicketStatus.SOLD, result.getStatus());
        assertEquals("customer", result.getCustomerLogin());
    }

    /** Проверяет отказ в покупке билета, который уже был продан. */
    @Test
    void buyRejectsAlreadySoldTicket() {
        Ticket ticket = newTicket();
        ticket.trySell(LocalDateTime.now(), customer);
        when(users.findById("customer")).thenReturn(Optional.of(customer));
        when(tickets.findById("TKT-000001")).thenReturn(Optional.of(ticket));

        assertThrows(IllegalStateException.class, () -> service.buy("TKT-000001", "customer"));
        verify(tickets, never()).save(any());
    }

    /** Проверяет корректную обработку ситуации, когда покупатель не найден в базе данных. */
    @Test
    void buyThrowsWhenUserDoesNotExist() {
        when(users.findById("customer")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.buy("TKT-000001", "customer"));
        verify(tickets, never()).save(any());
    }

    /** Проверяет запрет удаления билета, который уже был продан. */
    @Test
    void deleteAvailableRejectsSoldTicket() {
        Ticket ticket = newTicket();
        ticket.trySell(LocalDateTime.now(), customer);
        when(tickets.findById("TKT-000001")).thenReturn(Optional.of(ticket));

        assertThrows(IllegalStateException.class, () -> service.deleteAvailable("TKT-000001"));
        verify(tickets, never()).delete(any());
    }
}