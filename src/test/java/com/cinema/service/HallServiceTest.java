package com.cinema.service;

import com.cinema.TestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class HallServiceTest {

    private TestFixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new TestFixture();
        fixture.createSession();
    }

    @Test
    void delete_shouldRejectHallWithSessions() {
        assertThrows(IllegalStateException.class, () -> fixture.hallService.delete("H1"));
    }
}
