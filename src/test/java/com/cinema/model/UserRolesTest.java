package com.cinema.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserRolesTest {

    @Test
    void getRole_shouldReturnRoleOfEachSubclass() {
        User admin = new AdminUser("admin", "pass123", "Админ", "Админ", "a@a.test");
        User manager = new ManagerUser("manager", "pass123", "Иванов", "Макс", "m@a.test");
        User customer = new CustomerUser("anna", "pass123", "Петрова", "Анна", "c@a.test");

        assertEquals(UserRole.ADMIN, admin.getRole());
        assertEquals(UserRole.MANAGER, manager.getRole());
        assertEquals(UserRole.CUSTOMER, customer.getRole());
    }
}
