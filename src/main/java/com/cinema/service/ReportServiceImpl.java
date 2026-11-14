package com.cinema.service;

import com.cinema.model.TicketStatus;
import com.cinema.repository.FilmRepository;
import com.cinema.repository.FilmSessionRepository;
import com.cinema.repository.HallRepository;
import com.cinema.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/** Реализация ReportService. Содержит бизнес-логику формирования отчётов и агрегации статистики из разных репозиториев. */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final HallRepository halls;
    private final FilmRepository films;
    private final FilmSessionRepository sessions;
    private final TicketRepository tickets;

    /** Возвращает суммарную выручку сеанса после проверки его существования. */
    @Override
    public BigDecimal sessionRevenue(String sessionCode) {
        if (!sessions.existsById(sessionCode)) {
            throw new NoSuchElementException("Сеанс не найден: " + sessionCode);
        }
        return tickets.revenueBySession(sessionCode);
    }

    /** Преобразует результат JPQL-запроса в Map, сохраняя порядок сортировки по убыванию продаж. */
    @Override
    public Map<String, Long> soldTicketsByFilm() {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : tickets.soldCountByFilm()) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    /** Собирает общую статистику кинотеатра из четырёх репозиториев: залы, фильмы, сеансы, проданные билеты. */
    @Override
    public Statistics statistics() {
        return new Statistics(
                (int) halls.count(),
                (int) films.count(),
                (int) sessions.count(),
                tickets.countByStatus(TicketStatus.SOLD)
        );
    }
}