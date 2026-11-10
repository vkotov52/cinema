package com.cinema.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TicketTest {

    private Ticket ticket;

    @BeforeEach
    void setUp() {
        Hall hall = new Hall("H1", "Hall 1", 2, 2);
        Film film = new Film("FLM-1", "Test", 100, "12+", Set.of(new Genre("DRAMA", "Драма")));
        FilmSession session = new FilmSession(hall, film, LocalDateTime.of(2026, 10, 5, 18, 0),
                new BigDecimal("300.00"));
        ticket = new Ticket("TKT-000001", session, 1, 1, session.getBasePrice());
    }

    @Test
    void trySell_shouldSellAvailableTicket() {
        boolean sold = ticket.trySell("anna", LocalDateTime.now());

        assertTrue(sold);
        assertEquals(TicketStatus.SOLD, ticket.getStatus());
        assertEquals("anna", ticket.getCustomerLogin());
        assertNotNull(ticket.getSoldAt());
    }

    @Test
    void trySell_shouldRejectSecondSale() {
        ticket.trySell("anna", LocalDateTime.now());

        assertFalse(ticket.trySell("boris", LocalDateTime.now()));
        assertEquals("anna", ticket.getCustomerLogin());
    }
}
