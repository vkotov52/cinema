package com.cinema.service;

/** Общая статистика кинотеатра. */
public record Statistics(int halls, int films, int sessions, long soldTickets) {
}
