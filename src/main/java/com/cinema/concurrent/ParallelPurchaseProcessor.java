package com.cinema.concurrent;

import com.cinema.service.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/** Многопоточная обработка одной коллекции (очереди заявок).*/
@Component
public class ParallelPurchaseProcessor implements PurchaseProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ParallelPurchaseProcessor.class);
    private static final int PAYMENT_MILLIS = 50;

    private final TicketService ticketService;

    public ParallelPurchaseProcessor(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Override
    public int process(Queue<PurchaseRequest> requests, int cashiers) {
        if (cashiers <= 0) {
            throw new IllegalArgumentException("Количество кассиров должно быть больше нуля");
        }
        AtomicInteger sold = new AtomicInteger();
        ExecutorService executor = Executors.newFixedThreadPool(cashiers, new NamedThreadFactory("Cashier"));
        try {
            List<Callable<Void>> workers = new ArrayList<>();
            for (int i = 0; i < cashiers; i++) {
                workers.add(() -> {
                    handleRequests(requests, sold);
                    return null;
                });
            }
            for (Future<Void> future : executor.invokeAll(workers)) {
                future.get();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Обработка заявок прервана", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Ошибка при обработке заявок", e.getCause());
        } finally {
            executor.shutdown();
        }
        return sold.get();
    }

    private void handleRequests(Queue<PurchaseRequest> requests, AtomicInteger sold) {
        PurchaseRequest request = requests.poll();
        while (request != null) {
            try {
                Thread.sleep(PAYMENT_MILLIS);
                ticketService.buy(request.ticketNumber(), request.customerLogin());
                sold.incrementAndGet();
                LOG.info("{}, {}: билет куплен", request.ticketNumber(), request.customerLogin());
            } catch (IllegalStateException | IllegalArgumentException e) {
                LOG.info("{}, {}: заявка отклонена ({})", request.ticketNumber(), request.customerLogin(),
                        e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            request = requests.poll();
        }
    }
}
