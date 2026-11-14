package com.cinema.service;

import com.cinema.model.Hall;
import com.cinema.repository.FilmSessionRepository;
import com.cinema.repository.HallRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = HallServiceImpl.class)
class HallServiceImplTest {

    @MockitoBean
    private HallRepository halls;

    @MockitoBean
    private FilmSessionRepository sessions;

    @Autowired
    private HallService service;

    /** Проверяет успешное создание зала и корректность расчёта вместимости (ряды × места в ряду). */
    @Test
    void createSavesHall() {
        when(halls.save(any(Hall.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Hall hall = service.create("H1", "Большой зал", 10, 12);

        assertEquals("H1", hall.getHallCode());
        assertEquals(120, hall.capacity());
    }

    /** Проверяет запрет изменения количества рядов у зала с сеансами. */
    @Test
    void updateRejectsLayoutChangeWhenHallHasSessions() {
        when(halls.findById("H1")).thenReturn(Optional.of(new Hall("H1", "Зал", 5, 10)));
        when(sessions.existsByHall_HallCode("H1")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.update("H1", "Зал", 6, 10));
        verify(halls, never()).save(any());
    }

    /** Проверяет, что переименование зала разрешено даже при наличии сеансов, так как схема мест не затрагивается. */
    @Test
    void updateAllowsRenamingHallWithSessions() {
        when(halls.findById("H1")).thenReturn(Optional.of(new Hall("H1", "Зал", 5, 10)));
        when(sessions.existsByHall_HallCode("H1")).thenReturn(true);
        when(halls.save(any(Hall.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Hall result = service.update("H1", "Новый зал", 5, 10);

        assertEquals("Новый зал", result.getName());
    }

    /** Проверяет бизнес-правило: зал нельзя удалить, если для него существует хотя бы один сеанс. */
    @Test
    void deleteRejectsHallWithSessions() {
        when(halls.findById("H1")).thenReturn(Optional.of(new Hall("H1", "Зал", 5, 10)));
        when(sessions.existsByHall_HallCode("H1")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.delete("H1"));
        verify(halls, never()).delete(any());
    }
}