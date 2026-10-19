package com.cinema.concurrent;

import java.util.Queue;

public interface PurchaseProcessor {

    /**
     * Несколько кассиров (потоки Cashier-N) параллельно разбирают очередь заявок.
     * Возвращает число проданных билетов.
     */
    int process(Queue<PurchaseRequest> requests, int cashiers);
}
