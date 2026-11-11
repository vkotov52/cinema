package com.cinema.controller;

import com.cinema.controller.dto.BuyTicketRequest;
import com.cinema.model.Ticket;
import com.cinema.model.TicketStatus;
import com.cinema.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

/** REST-контроллер для управления билетами. Обрабатывает поиск, покупку и удаление билетов. */
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService service;

    /** Возвращает список билетов с фильтрацией по сеансу, статусу или покупателю (HTTP 200). */
    @GetMapping
    public List<Ticket> findAll(
            @RequestParam(required = false) String sessionCode,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) String customerLogin
    ) {
        if (customerLogin != null) {
            return service.findByCustomer(customerLogin);
        }
        if (sessionCode != null && status == TicketStatus.AVAILABLE) {
            return service.findAvailable(sessionCode);
        }
        if (sessionCode != null) {
            return service.findBySession(sessionCode);
        }
        if (status != null) {
            return service.findByStatus(status);
        }
        return service.findAll();
    }

    /** Возвращает билет по конкретному месту в сеансе или выбрасывает NoSuchElementException (HTTP 200 / 404). */
    @GetMapping("/seat")
    public Ticket findBySeat(
            @RequestParam String sessionCode,
            @RequestParam int row,
            @RequestParam int seat
    ) {
        return service.findBySeat(sessionCode, row, seat)
                .orElseThrow(() -> new NoSuchElementException("Место не найдено"));
    }

    /** Возвращает билет по его номеру или выбрасывает NoSuchElementException (HTTP 200 / 404). */
    @GetMapping("/{number}")
    public Ticket findById(@PathVariable String number) {
        return service.findById(number);
    }

    /** Покупает билет по номеру и привязывает его к покупателю (HTTP 200). */
    @PostMapping("/{number}/purchase")
    public Ticket buy(@PathVariable String number, @Valid @RequestBody BuyTicketRequest request) {
        return service.buy(number, request.login());
    }

    /** Удаляет только свободный билет. Тело ответа пустое (HTTP 204 No Content). */
    @DeleteMapping("/{number}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String number) {
        service.deleteAvailable(number);
    }
}