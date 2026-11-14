package com.cinema.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelTest {

    private final LocalDateTime start = LocalDateTime.of(2026, 11, 20, 12, 0);

    private FilmSession session() {
        Film film = new Film("F1", "Фильм", 120, "12+", Set.of(new Genre("DRAMA", "Драма")));
        Hall hall = new Hall("H1", "Зал 1", 2, 2);
        return new FilmSession(hall, film, start, BigDecimal.TEN);
    }

    /** Проверяет корректный расчёт общей вместимости зала (количество рядов умножить на количество мест в ряду). */
    @Test
    void hallCalculatesCapacity() {
        assertEquals(30, new Hall("H1", "Зал 1", 5, 6).capacity());
    }

    /** Проверяет, что каждый подкласс пользователя при создании автоматически получает свою корректную роль. */
    @Test
    void userSubclassesKeepTheirRoles() {
        assertEquals(UserRole.ADMIN, new AdminUser("admin", "pass", "Иванов", "Иван", "admin@mail.ru").getRole());
        assertEquals(UserRole.MANAGER, new ManagerUser("manager", "pass", "Петров", "Пётр", "manager@mail.ru").getRole());
        assertEquals(UserRole.CUSTOMER, new CustomerUser("customer", "pass", "Сидоров", "Сидор", "customer@mail.ru").getRole());
    }

    /** Проверяет корректную проверку наличия жанра у фильма: true для существующего и false для отсутствующего. */
    @Test
    void filmKnowsItsGenres() {
        Film film = new Film("F1", "Фильм", 90, "12+", Set.of(new Genre("DRAMA", "Драма")));

        assertTrue(film.hasGenre("DRAMA"));
        assertFalse(film.hasGenre("COMEDY"));
    }

    /** Проверяет автоматическую генерацию кода сеанса, расчёт времени окончания и корректное определение пересечения интервалов. */
    @Test
    void sessionBuildsCodeAndDetectsOverlap() {
        FilmSession session = session();

        assertEquals("H1-20261120-1200", session.getSessionCode());
        assertEquals(start.plusMinutes(120), session.getEndTime());
        assertTrue(session.overlaps(start.plusMinutes(60), start.plusMinutes(180)));
        assertFalse(session.overlaps(start.plusMinutes(120), start.plusMinutes(200)));
    }

    /** Проверяет успешную продажу свободного билета: смену статуса на SOLD и привязку логина покупателя. */
    @Test
    void trySellSuccessfullyBindsCustomerAndChangesStatus() {
        Ticket ticket = new Ticket("TKT-000001", session(), 1, 1, BigDecimal.TEN);
        User customer = new CustomerUser("ivanov", "password", "Иванов", "Иван", "ivanov@mail.ru");

        boolean isSold = ticket.trySell(LocalDateTime.now(), customer);

        assertTrue(isSold);
        assertEquals(TicketStatus.SOLD, ticket.getStatus());
        assertEquals("ivanov", ticket.getCustomerLogin());
    }

    /** Проверяет защиту от повторной продажи: попытка продать уже проданный билет должна быть отклонена. */
    @Test
    void trySellRejectsSecondAttemptForAlreadySoldTicket() {
        Ticket ticket = new Ticket("TKT-000001", session(), 1, 1, BigDecimal.TEN);
        User customer = new CustomerUser("ivanov", "password", "Иванов", "Иван", "ivanov@mail.ru");

        ticket.trySell(LocalDateTime.now(), customer);
        boolean secondAttempt = ticket.trySell(LocalDateTime.now(), customer);

        assertFalse(secondAttempt);
    }
}