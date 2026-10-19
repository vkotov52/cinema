package com.cinema.repository;

import com.cinema.model.Hall;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryRepositoryTest {

    private EntityRepository<Hall, String> repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRepository<>(Hall::getHallCode);
        repository.save(new Hall("B", "Second", 2, 2));
        repository.save(new Hall("A", "First", 3, 3));
    }

    @Test
    void save_shouldStoreEntitiesSortedByKey() {
        assertEquals(2, repository.findAll().size());
        assertEquals("A", repository.findAll().getFirst().getHallCode());
        assertEquals("Second", repository.findByKey("B").orElseThrow().getName());
    }

    @Test
    void deleteByKey_shouldRemoveEntity() {
        assertTrue(repository.deleteByKey("A"));

        assertTrue(repository.findByKey("A").isEmpty());
        assertFalse(repository.deleteByKey("A"));
    }
}
