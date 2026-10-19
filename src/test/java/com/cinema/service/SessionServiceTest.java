package com.cinema.service;

import com.cinema.TestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SessionServiceTest {

    private TestFixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new TestFixture();
        fixture.createSession();
    }

    @Test
    void create_shouldCreateTicketForEverySeat() {
        assertEquals(6, fixture.ticketService.findAvailable(TestFixture.SESSION_CODE).size());
    }

    @Test
    void create_shouldRejectSecondSessionInSameHallAtSameTime() {
        assertThrows(IllegalStateException.class,
                () -> fixture.sessionService.create("H1", "FLM-1", TestFixture.EVENING, BigDecimal.TEN));
    }

    @Test
    void delete_shouldRejectSessionWithSoldTicket() {
        fixture.createCustomer("anna");
        String ticketNumber = fixture.ticketService.findAvailable(TestFixture.SESSION_CODE).getFirst().getTicketNumber();
        fixture.ticketService.buy(ticketNumber, "anna");

        assertThrows(IllegalStateException.class, () -> fixture.sessionService.delete(TestFixture.SESSION_CODE));
    }
}
