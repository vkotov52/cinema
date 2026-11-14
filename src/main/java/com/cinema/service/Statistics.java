package com.cinema.service;

/** Агрегированная статистика кинотеатра. Возвращается методом ReportService.statistics(). */
public record Statistics(int halls, int films, int sessions, long soldTickets) {
}
