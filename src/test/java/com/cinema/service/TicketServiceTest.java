package com.cinema.service;

import com.cinema.TestFixture;
import com.cinema.model.Ticket;
import com.cinema.model.TicketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TicketServiceTest {

    private TestFixture fixture;
    private String ticketNumber;

    @BeforeEach
    void setUp() {
        fixture = new TestFixture();
        fixture.createSession();
        fixture.createCustomer("anna");
        fixture.createCustomer("boris");
        ticketNumber = fixture.ticketService.findAvailable(TestFixture.SESSION_CODE).getFirst().getTicketNumber();
    }

    @Test
    void buy_shouldMarkTicketSoldForCustomer() {
        Ticket ticket = fixture.ticketService.buy(ticketNumber, "anna");

        assertEquals(TicketStatus.SOLD, ticket.getStatus());
        assertEquals("anna", ticket.getCustomerLogin());
        assertNotNull(ticket.getSoldAt());
    }

    @Test
    void buy_shouldRejectAlreadySoldTicket() {
        fixture.ticketService.buy(ticketNumber, "anna");

        assertThrows(IllegalStateException.class, () -> fixture.ticketService.buy(ticketNumber, "boris"));
    }

    @Test
    void findByCustomer_shouldReturnBoughtTickets() {
        fixture.ticketService.buy(ticketNumber, "anna");
        String second = fixture.ticketService.findAvailable(TestFixture.SESSION_CODE).getFirst().getTicketNumber();
        fixture.ticketService.buy(second, "anna");

        assertEquals(2, fixture.ticketService.findByCustomer("anna").size());
        assertEquals(0, fixture.ticketService.findByCustomer("boris").size());
    }
}
