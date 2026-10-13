package com.cinema.concurrent;

import com.cinema.model.Ticket;
import com.cinema.model.User;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/** Заявка на покупку билета. Заявки лежат в очереди, пока их не заберёт кассир. */
public record PurchaseRequest(String ticketNumber, String customerLogin) {

    /**
     * Строит очередь из заданного количества заявок. Билетов выбирается вдвое меньше, чем заявок,
     * поэтому на каждый билет приходится несколько заявок. Покупатели берутся по кругу.
     */
    public static Queue<PurchaseRequest> queueOf(List<Ticket> tickets, List<User> customers, int count) {
        if (tickets.isEmpty()) {
            throw new IllegalStateException("Нет свободных билетов");
        }
        if (customers.isEmpty()) {
            throw new IllegalStateException("Нет зарегистрированных покупателей");
        }
        int targets = Math.clamp(count / 2, 1, tickets.size());

        Queue<PurchaseRequest> queue = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < count; i++) {
            queue.add(new PurchaseRequest(tickets.get(i % targets).getTicketNumber(),
                    customers.get(i % customers.size()).getLogin()));
        }
        return queue;
    }
}
