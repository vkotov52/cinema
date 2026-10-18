package com.cinema.service;

import java.math.BigDecimal;
import java.util.Map;

public interface ReportService {

    /** Сумма цен проданных билетов выбранного сеанса. */
    BigDecimal sessionRevenue(String sessionCode);

    /** Название фильма и число проданных билетов, по убыванию. */
    Map<String, Long> soldTicketsByFilm();

    Statistics statistics();
}
