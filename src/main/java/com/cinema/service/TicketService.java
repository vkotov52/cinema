package com.cinema.service;

import com.cinema.model.Ticket;
import com.cinema.model.TicketStatus;

import java.util.List;
import java.util.Optional;

/** Сервис для управления билетами. */
public interface TicketService {

    /** Возвращает список всех билетов. */
    List<Ticket> findAll();

    /** Возвращает билет по его номеру или выбрасывает NoSuchElementException. */
    Ticket findById(String number);

    /** Возвращает все билеты с заданным статусом (AVAILABLE, RESERVED или SOLD). */
    List<Ticket> findByStatus(TicketStatus status);

    /** Возвращает все билеты заданного сеанса. */
    List<Ticket> findBySession(String sessionCode);

    /** Возвращает только свободные (AVAILABLE) билеты заданного сеанса. */
    List<Ticket> findAvailable(String sessionCode);

    /** Возвращает билет по сеансу, ряду и месту, или empty, если такого места нет. */
    Optional<Ticket> findBySeat(String sessionCode, int row, int seat);

    /** Возвращает все билеты, купленные пользователем с заданным логином. */
    List<Ticket> findByCustomer(String login);

    /** Покупает билет: привязывает его к покупателю, фиксирует время продажи и меняет статус на SOLD. */
    Ticket buy(String number, String login);

    /** Удаляет билет, только если он имеет статус AVAILABLE. Запрещено удалять проданные билеты. */
    void deleteAvailable(String number);
}