package com.cinema.service;

import com.cinema.TestFixture;
import com.cinema.model.AdminUser;
import com.cinema.model.CustomerUser;
import com.cinema.model.ManagerUser;
import com.cinema.model.User;
import com.cinema.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {

    private TestFixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new TestFixture();
    }

    @Test
    void register_shouldCreateSubclassMatchingRole() {
        User admin = fixture.userService.register(UserRole.ADMIN, "admin", "pass123", "Админ", "Админ", "a@a.test");
        User manager = fixture.userService.register(UserRole.MANAGER, "manager", "pass123", "Иванов", "Макс", "m@a.test");
        User customer = fixture.userService.register(UserRole.CUSTOMER, "anna", "pass123", "Петрова", "Анна", "c@a.test");

        assertTrue(admin instanceof AdminUser);
        assertTrue(manager instanceof ManagerUser);
        assertTrue(customer instanceof CustomerUser);
        assertEquals("pass123", customer.getPassword());
        assertEquals(1, fixture.userService.findByRole(UserRole.CUSTOMER).size());
    }

    @Test
    void register_shouldRejectDuplicateLogin() {
        fixture.createCustomer("anna");

        assertThrows(IllegalStateException.class, () -> fixture.userService.register(
                UserRole.CUSTOMER, "anna", "pass123", "Иванова", "Анна", "other@mail.test"));
    }

    @Test
    void delete_shouldRejectUserWithTickets() {
        fixture.createSession();
        fixture.createCustomer("anna");
        String ticketNumber = fixture.ticketService.findAvailable(TestFixture.SESSION_CODE).getFirst().getTicketNumber();
        fixture.ticketService.buy(ticketNumber, "anna");

        assertThrows(IllegalStateException.class, () -> fixture.userService.delete("anna"));
    }
}
