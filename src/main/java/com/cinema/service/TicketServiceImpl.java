package com.cinema.service;

import com.cinema.model.Ticket;
import com.cinema.model.TicketStatus;
import com.cinema.model.User;
import com.cinema.repository.TicketRepository;
import com.cinema.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/** Реализация TicketService. Содержит бизнес-логику поиска, покупки и удаления билетов. */
@Service
@Transactional
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository tickets;
    private final UserRepository users;

    /** Возвращает список всех билетов. */
    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findAll() {
        return tickets.findAll();
    }

    /** Возвращает билет по номеру или выбрасывает NoSuchElementException, если билет не найден. */
    @Override
    @Transactional(readOnly = true)
    public Ticket findById(String number) {
        return tickets.findById(number)
                .orElseThrow(() -> new NoSuchElementException("Билет не найден: " + number));
    }

    /** Возвращает все билеты с заданным статусом. */
    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findByStatus(TicketStatus status) {
        return tickets.findByStatus(status);
    }

    /** Возвращает все билеты заданного сеанса. */
    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findBySession(String sessionCode) {
        return tickets.findBySession_SessionCode(sessionCode);
    }

    /** Возвращает только свободные (AVAILABLE) билеты заданного сеанса. */
    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findAvailable(String sessionCode) {
        return tickets.findBySession_SessionCodeAndStatus(sessionCode, TicketStatus.AVAILABLE);
    }

    /** Возвращает билет по сеансу, ряду и месту, или empty, если такого места нет. */
    @Override
    @Transactional(readOnly = true)
    public Optional<Ticket> findBySeat(String sessionCode, int row, int seat) {
        return tickets.findBySession_SessionCodeAndRowNumAndSeatNum(sessionCode, row, seat);
    }

    /** Возвращает все билеты, купленные пользователем с заданным логином. */
    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findByCustomer(String login) {
        return tickets.findByCustomer_Login(login);
    }

    /** Покупает билет: находит пользователя и билет, затем делегирует смену статуса методу trySell сущности Ticket. */
    @Override
    public Ticket buy(String number, String login) {
        User user = users.findById(login)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден: " + login));
        Ticket ticket = findById(number);

        if (!ticket.trySell(LocalDateTime.now(), user)) {
            throw new IllegalStateException("Билет уже продан или недоступен");
        }
        return tickets.save(ticket);
    }

    /** Удаляет билет, только если он имеет статус AVAILABLE. Запрещено удалять проданные билеты. */
    @Override
    public void deleteAvailable(String number) {
        Ticket ticket = findById(number);
        if (ticket.getStatus() != TicketStatus.AVAILABLE) {
            throw new IllegalStateException("Удалять можно только доступные билеты");
        }
        tickets.delete(ticket);
    }
}