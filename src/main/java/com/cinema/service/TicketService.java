package com.cinema.service;

import com.cinema.model.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketService {

    List<Ticket> findAvailable(String sessionCode);

    Optional<Ticket> findBySeat(String sessionCode, int rowNum, int seatNum);

    List<Ticket> findByCustomer(String login);

    Ticket buy(String ticketNumber, String login);
}
