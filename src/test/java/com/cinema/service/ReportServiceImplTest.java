package com.cinema.service;

import com.cinema.model.TicketStatus;
import com.cinema.repository.FilmRepository;
import com.cinema.repository.FilmSessionRepository;
import com.cinema.repository.HallRepository;
import com.cinema.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ReportServiceImpl.class)
class ReportServiceImplTest {

    @MockitoBean
    private HallRepository halls;

    @MockitoBean
    private FilmRepository films;

    @MockitoBean
    private FilmSessionRepository sessions;

    @MockitoBean
    private TicketRepository tickets;

    @Autowired
    private ReportService service;

    /** Проверяет, что общая статистика корректно агрегирует значения из четырёх репозиториев в единый объект Statistics. */
    @Test
    void statisticsCountsStoredEntities() {
        when(halls.count()).thenReturn(2L);
        when(films.count()).thenReturn(3L);
        when(sessions.count()).thenReturn(4L);
        when(tickets.countByStatus(TicketStatus.SOLD)).thenReturn(5L);

        assertEquals(new Statistics(2, 3, 4, 5L), service.statistics());
    }

    /** Проверяет, что запрос выручки по несуществующему сеансу завершается ошибкой до обращения к репозиторию билетов. */
    @Test
    void sessionRevenueThrowsForUnknownSession() {
        when(sessions.existsById("H1-20261120-1200")).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> service.sessionRevenue("H1-20261120-1200"));
    }

    /** Проверяет, что LinkedHashMap сохраняет порядок сортировки по убыванию продаж, возвращённый JPQL-запросом. */
    @Test
    void soldTicketsByFilmKeepsRepositoryOrder() {
        when(tickets.soldCountByFilm()).thenReturn(
                List.<Object[]>of(new Object[]{"Фильм А", 3L}, new Object[]{"Фильм Б", 1L})
        );

        Map<String, Long> result = service.soldTicketsByFilm();

        assertEquals(List.of("Фильм А", "Фильм Б"), List.copyOf(result.keySet()));
        assertEquals(3L, result.get("Фильм А"));
    }
}