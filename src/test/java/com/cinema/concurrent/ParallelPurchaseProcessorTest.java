package com.cinema.concurrent;

import com.cinema.TestFixture;
import com.cinema.model.Ticket;
import com.cinema.model.TicketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParallelPurchaseProcessorTest {

    private static final int CUSTOMERS = 20;

    private TestFixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new TestFixture();
        fixture.createSession();
        for (int i = 0; i < CUSTOMERS; i++) {
            fixture.createCustomer("buyer" + i);
        }
    }

    @Test
    void process_shouldSellAllDifferentTickets() {
        List<Ticket> free = fixture.ticketService.findAvailable(TestFixture.SESSION_CODE);
        Queue<PurchaseRequest> queue = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < free.size(); i++) {
            queue.add(new PurchaseRequest(free.get(i).getTicketNumber(), "buyer" + i));
        }

        int sold = fixture.purchaseProcessor.process(queue, 3);

        assertEquals(free.size(), sold);
    }

    @Test
    void process_shouldSellSameTicketOnlyOnce() {
        String ticketNumber = fixture.ticketService.findAvailable(TestFixture.SESSION_CODE).getFirst().getTicketNumber();
        Queue<PurchaseRequest> queue = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < CUSTOMERS; i++) {
            queue.add(new PurchaseRequest(ticketNumber, "buyer" + i));
        }

        int sold = fixture.purchaseProcessor.process(queue, 8);

        assertEquals(1, sold);
        assertEquals(TicketStatus.SOLD, fixture.tickets.findByKey(ticketNumber).orElseThrow().getStatus());
    }
}
