package com.cinema.controller;

import com.cinema.service.ReportService;
import com.cinema.service.Statistics;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

/** REST-контроллер для отчётов и аналитики. Возвращает статистику по сеансам, фильмам и кинотеатру. */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reports;

    /** Возвращает суммарную выручку сеанса по проданным билетам (HTTP 200). */
    @GetMapping("/sessions/{sessionCode}/revenue")
    public BigDecimal sessionRevenue(@PathVariable String sessionCode) {
        return reports.sessionRevenue(sessionCode);
    }

    /** Возвращает количество проданных билетов по каждому фильму в порядке убывания (HTTP 200). */
    @GetMapping("/films/sold-tickets")
    public Map<String, Long> soldTicketsByFilm() {
        return reports.soldTicketsByFilm();
    }

    /** Возвращает общую статистику кинотеатра: количество залов, фильмов, сеансов и проданных билетов (HTTP 200). */
    @GetMapping("/statistics")
    public Statistics statistics() {
        return reports.statistics();
    }
}