package com.cinema.service;

import com.cinema.TestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportServiceTest {

    private TestFixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new TestFixture();
        fixture.createSession();
        fixture.createCustomer("anna");
        fixture.filmService.create("FLM-2", "Other Film", 90, "6+", Set.of("DRAMA"));
        for (int i = 0; i < 2; i++) {
            String ticketNumber = fixture.ticketService.findAvailable(TestFixture.SESSION_CODE).getFirst().getTicketNumber();
            fixture.ticketService.buy(ticketNumber, "anna");
        }
    }

    @Test
    void sessionRevenue_shouldSumPricesOfSoldTickets() {
        BigDecimal revenue = fixture.reportService.sessionRevenue(TestFixture.SESSION_CODE);

        assertEquals(0, new BigDecimal("600.00").compareTo(revenue));
    }

    @Test
    void soldTicketsByFilm_shouldCountPerFilmInDescendingOrder() {
        Map<String, Long> sales = fixture.reportService.soldTicketsByFilm();

        assertEquals("Test Film", sales.keySet().iterator().next());
        assertEquals(2L, sales.get("Test Film").longValue());
        assertEquals(0L, sales.get("Other Film").longValue());
    }

    @Test
    void statistics_shouldCountEntities() {
        Statistics statistics = fixture.reportService.statistics();

        assertEquals(1, statistics.halls());
        assertEquals(2, statistics.films());
        assertEquals(1, statistics.sessions());
        assertEquals(2L, statistics.soldTickets());
    }
}
