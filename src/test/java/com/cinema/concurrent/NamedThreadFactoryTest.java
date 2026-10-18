package com.cinema.concurrent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NamedThreadFactoryTest {

    @Test
    void newThread_shouldNameThreadsWithPrefixAndCounter() {
        NamedThreadFactory factory = new NamedThreadFactory("Cashier");

        assertEquals("Cashier-1", factory.newThread(() -> { }).getName());
        assertEquals("Cashier-2", factory.newThread(() -> { }).getName());
    }
}
