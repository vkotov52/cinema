package com.cinema.service;

import java.math.BigDecimal;
import java.util.Map;

/** Сервис для формирования отчётов и аналитики. */
public interface ReportService {

    /** Возвращает суммарную выручку сеанса по проданным билетам. */
    BigDecimal sessionRevenue(String sessionCode);

    /** Возвращает количество проданных билетов по каждому фильму. */
    Map<String, Long> soldTicketsByFilm();

    /** Возвращает общую статистику кинотеатра: количество залов, фильмов, сеансов и проданных билетов. */
    Statistics statistics();
}