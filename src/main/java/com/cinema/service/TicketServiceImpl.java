package com.cinema.service;

import com.cinema.model.Ticket;
import com.cinema.model.TicketStatus;
import com.cinema.model.User;
import com.cinema.repository.EntityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    private final EntityRepository<Ticket, String> tickets;
    private final EntityRepository<User, String> users;

    public TicketServiceImpl(EntityRepository<Ticket, String> tickets, EntityRepository<User, String> users) {
        this.tickets = tickets;
        this.users = users;
    }

    @Override
    public List<Ticket> findAvailable(String sessionCode) {
        List<Ticket> result = new ArrayList<>();
        for (Ticket ticket : tickets.findAll()) {
            if (ticket.getSessionCode().equals(sessionCode) && ticket.getStatus() == TicketStatus.AVAILABLE) {
                result.add(ticket);
            }
        }
        return result;
    }

    @Override
    public Optional<Ticket> findBySeat(String sessionCode, int rowNum, int seatNum) {
        for (Ticket ticket : tickets.findAll()) {
            if (ticket.getSessionCode().equals(sessionCode)
                    && ticket.getRowNum() == rowNum && ticket.getSeatNum() == seatNum) {
                return Optional.of(ticket);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Ticket> findByCustomer(String login) {
        List<Ticket> result = new ArrayList<>();
        for (Ticket ticket : tickets.findAll()) {
            if (login.equals(ticket.getCustomerLogin())) {
                result.add(ticket);
            }
        }
        return result;
    }

    @Override
    public Ticket buy(String ticketNumber, String login) {
        users.findByKey(login).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + login));
        Ticket ticket = tickets.findByKey(ticketNumber)
                .orElseThrow(() -> new IllegalArgumentException("Билет не найден: " + ticketNumber));
        if (!ticket.trySell(login, LocalDateTime.now())) {
            throw new IllegalStateException("Билет " + ticketNumber + " уже продан");
        }
        return ticket;
    }
}
