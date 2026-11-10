package com.cinema.cli;

import com.cinema.concurrent.PurchaseProcessor;
import com.cinema.concurrent.PurchaseRequest;
import com.cinema.model.Ticket;
import com.cinema.model.UserRole;
import com.cinema.service.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Queue;
import java.util.Set;

@Component
public class DemoRunner {

    private static final String IMAX_SESSION = "IMAX-20261005-1800";
    private static final int REQUESTS = 12;
    private static final int CASHIERS = 4;

    private final HallService hallService;
    private final GenreService genreService;
    private final FilmService filmService;
    private final SessionService sessionService;
    private final UserService userService;
    private final TicketService ticketService;
    private final ReportService reportService;
    private final PurchaseProcessor purchaseProcessor;
    private final EntityPrinter printer;
    private final ConsoleIO io;

    public DemoRunner(HallService hallService, GenreService genreService, FilmService filmService,
                      SessionService sessionService, UserService userService, TicketService ticketService,
                      ReportService reportService, PurchaseProcessor purchaseProcessor, EntityPrinter printer,
                      ConsoleIO io) {
        this.hallService = hallService;
        this.genreService = genreService;
        this.filmService = filmService;
        this.sessionService = sessionService;
        this.userService = userService;
        this.ticketService = ticketService;
        this.reportService = reportService;
        this.purchaseProcessor = purchaseProcessor;
        this.printer = printer;
        this.io = io;
    }

    public void run() {
        if (!hallService.findAll().isEmpty()) {
            io.println("Ошибка инициализации: демонстрация требует пустой базы данных.");
            return;
        }
        addData();
        showData();
        search();
        buyTickets();
        deleteData();
        processQueue();
        showReports();
    }

    private void addData() {
        io.println("\n=== 1. Инициализация тестовых данных ===");
        hallService.create("IMAX", "IMAX Hall", 5, 8);
        hallService.create("H2", "Hall 2", 4, 6);
        genreService.create("SCIFI", "Фантастика");
        genreService.create("DRAMA", "Драма");
        genreService.create("ACTION", "Боевик");
        filmService.create("FLM-0001", "Interstellar", 169, "12+", Set.of("SCIFI", "DRAMA"));
        filmService.create("FLM-0002", "Dune: Part Two", 166, "12+", Set.of("SCIFI", "ACTION"));
        filmService.create("FLM-0003", "The Matrix", 136, "16+", Set.of("SCIFI", "ACTION"));

        userService.register(UserRole.ADMIN, "admin", "pass123", "Смирнов", "Алексей", "admin@cinema.test");
        userService.register(UserRole.MANAGER, "manager1", "pass123", "Волкова", "Мария", "maria@cinema.test");
        userService.register(UserRole.CUSTOMER, "ivanov_ii", "pass123", "Иванов", "Иван", "ivanov@mail.test");
        userService.register(UserRole.CUSTOMER, "petrova_as", "pass123", "Петрова", "Анна", "petrova@mail.test");
        userService.register(UserRole.CUSTOMER, "sidorov_bv", "pass123", "Сидоров", "Борис", "sidorov@mail.test");

        sessionService.create("IMAX", "FLM-0001", LocalDateTime.of(2026, 10, 5, 18, 0), new BigDecimal("500.00"));
        sessionService.create("IMAX", "FLM-0002", LocalDateTime.of(2026, 10, 5, 21, 30), new BigDecimal("600.00"));
        sessionService.create("H2", "FLM-0003", LocalDateTime.of(2026, 10, 5, 15, 0), new BigDecimal("350.00"));
        io.println("Успешно созданы: 2 зала, 3 жанра, 3 фильма, 5 учётных записей и 3 сеанса.");
    }

    private void showData() {
        io.println("\n=== 2. Вывод информации ===");
        io.println(printer.halls(hallService.findAll()));
        io.println(printer.genres(genreService.findAll()));
        io.println(printer.films(filmService.findAll()));
        io.println(printer.sessions(sessionService.findAll()));
        io.println(printer.users(userService.findAll()));
    }

    private void search() {
        io.println("\n=== 3. Поиск ===");
        io.println("Фильмы, в названии которых есть «dune»:");
        io.println(printer.films(filmService.searchByTitle("dune")));
        io.println("Фильмы жанра «Драма» (код DRAMA):");
        io.println(printer.films(filmService.findByGenre("DRAMA")));
        io.println("Сеансы зала IMAX:");
        io.println(printer.sessions(sessionService.findByHall("IMAX")));
        io.println("Пользователи с ролью CUSTOMER:");
        io.println(printer.users(userService.findByRole(UserRole.CUSTOMER)));
    }

    private void buyTickets() {
        io.println("\n=== 4. Сценарий покупки билетов (сеанс " + IMAX_SESSION + ") ===");
        List<Ticket> free = ticketService.findAvailable(IMAX_SESSION);

        for (int i = 0; i < 3; i++) {
            ticketService.buy(free.get(i).getTicketNumber(), "petrova_as");
        }
        for (int i = 3; i < 5; i++) {
            ticketService.buy(free.get(i).getTicketNumber(), "sidorov_bv");
        }

        io.println("Билеты пользователя 'petrova_as':");
        io.println(printer.customerTickets(ticketService.findByCustomer("petrova_as")));

        io.println("Оставшиеся свободные места на сеанс:");
        io.println(printer.freeSeats(ticketService.findAvailable(IMAX_SESSION)));

        io.println("Попытка покупки уже проданного билета " + free.getFirst().getTicketNumber() + " пользователем ivanov_ii:");
        tryAction("", () -> ticketService.buy(free.getFirst().getTicketNumber(), "ivanov_ii"));
    }

    private void deleteData() {
        io.println("\n=== 5. Удаление данных ===");
        tryAction("Зал IMAX успешно удалён.", () -> hallService.delete("IMAX"));
        tryAction("Учётная запись 'petrova_as' успешно удалена.", () -> userService.delete("petrova_as"));
        tryAction("Сеанс H2-20261005-1500 успешно удалён.", () -> sessionService.delete("H2-20261005-1500"));
        tryAction("Учётная запись 'ivanov_ii' успешно удалена.", () -> userService.delete("ivanov_ii"));

        io.println("Актуальное расписание сеансов после операций удаления:");
        io.println(printer.sessions(sessionService.findAll()));
    }

    private void processQueue() {
        io.println("\n=== 6. Многопоточная обработка: очередь из " + REQUESTS + " заявок, " + CASHIERS + " кассира ===");
        Queue<PurchaseRequest> queue = PurchaseRequest.queueOf(
                ticketService.findAvailable(IMAX_SESSION),
                userService.findByRole(UserRole.CUSTOMER), REQUESTS
        );
        int sold = purchaseProcessor.process(queue, CASHIERS);
        io.println("Продано билетов: " + sold + ", отклонено заявок: " + (REQUESTS - sold));
    }

    private void showReports() {
        io.println("\n=== 7. Отчёты ===");
        io.println("Выручка сеанса " + IMAX_SESSION + ": " + reportService.sessionRevenue(IMAX_SESSION));
        io.println(printer.filmSales(reportService.soldTicketsByFilm()));
        io.println(printer.statistics(reportService.statistics()));
    }

    private void tryAction(String successMessage, Runnable action) {
        try {
            action.run();
            if (!successMessage.isEmpty()) {
                io.println(successMessage);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            io.println("Отклонено: " + e.getMessage());
        }
    }
}