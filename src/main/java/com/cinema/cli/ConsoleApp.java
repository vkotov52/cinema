package com.cinema.cli;

import com.cinema.concurrent.PurchaseProcessor;
import com.cinema.concurrent.PurchaseRequest;
import com.cinema.model.Ticket;
import com.cinema.model.UserRole;
import com.cinema.service.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.function.IntConsumer;

@Component
public class ConsoleApp {

    private static final int MAX_REQUESTS = 100;
    private static final int MAX_CASHIERS = 8;

    private final HallService hallService;
    private final GenreService genreService;
    private final FilmService filmService;
    private final SessionService sessionService;
    private final UserService userService;
    private final TicketService ticketService;
    private final ReportService reportService;
    private final PurchaseProcessor purchaseProcessor;
    private final DemoRunner demoRunner;
    private final ConsoleIO io;
    private final EntityPrinter printer;

    public ConsoleApp(HallService hallService, GenreService genreService, FilmService filmService,
                      SessionService sessionService, UserService userService, TicketService ticketService,
                      ReportService reportService, PurchaseProcessor purchaseProcessor, DemoRunner demoRunner,
                      ConsoleIO io, EntityPrinter printer) {
        this.hallService = hallService;
        this.genreService = genreService;
        this.filmService = filmService;
        this.sessionService = sessionService;
        this.userService = userService;
        this.ticketService = ticketService;
        this.reportService = reportService;
        this.purchaseProcessor = purchaseProcessor;
        this.demoRunner = demoRunner;
        this.io = io;
        this.printer = printer;
    }

    public void run() {
        try {
            runMenu("Система управления кинотеатром",
                    List.of("Залы", "Фильмы и жанры", "Сеансы", "Пользователи", "Билеты", "Отчёты и аналитика",
                            "Многопоточные операции", "Демонстрация"),
                    "Выход", choice -> {
                        switch (choice) {
                            case 1 -> hallsMenu();
                            case 2 -> filmsMenu();
                            case 3 -> sessionsMenu();
                            case 4 -> usersMenu();
                            case 5 -> ticketsMenu();
                            case 6 -> reportsMenu();
                            case 7 -> runCashiers();
                            case 8 -> demoRunner.run();
                            default -> { }
                        }
                    });
        } catch (NoSuchElementException e) {
            io.println("\nВвод завершён.");
        }
    }

    private void runMenu(String title, List<String> items, String exitLabel, IntConsumer handler) {
        while (true) {
            io.println("\n=== " + title + " ===");
            for (int i = 0; i < items.size(); i++) {
                io.println("[" + (i + 1) + "] " + items.get(i));
            }
            io.println("[0] " + exitLabel);
            int choice = io.readInt("Ваш выбор: ");
            if (choice == 0) {
                return;
            }
            if (choice < 1 || choice > items.size()) {
                io.println("Нет такого пункта");
                continue;
            }
            try {
                handler.accept(choice);
            } catch (IllegalArgumentException | IllegalStateException e) {
                io.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void hallsMenu() {
        runMenu("Залы", List.of("Показать", "Добавить", "Удалить"), "Назад", choice -> {
            switch (choice) {
                case 1 -> io.println(printer.halls(hallService.findAll()));
                case 2 -> {
                    hallService.create(io.readCode("Код зала: "), io.readLine("Название: "),
                            io.readInt("Рядов: "), io.readInt("Мест в ряду: "));
                    io.println("Зал добавлен.");
                }
                case 3 -> {
                    hallService.delete(io.readCode("Код зала: "));
                    io.println("Зал удалён.");
                }
            }
        });
    }

    private void filmsMenu() {
        runMenu("Фильмы и жанры",
                List.of("Показать фильмы", "Добавить фильм", "Найти по названию", "Найти по жанру", "Удалить фильм",
                        "Показать жанры", "Добавить жанр", "Удалить жанр"),
                "Назад", choice -> {
                    switch (choice) {
                        case 1 -> io.println(printer.films(filmService.findAll()));
                        case 2 -> {
                            filmService.create(io.readCode("Код фильма: "), io.readLine("Название: "),
                                    io.readInt("Длительность, минут: "),
                                    io.readLine("Возрастной рейтинг (0+, 6+, 12+, 16+, 18+): "),
                                    io.readCodes("Коды жанров через запятую: "));
                            io.println("Фильм добавлен.");
                        }
                        case 3 -> io.println(printer.films(filmService.searchByTitle(io.readLine("Часть названия: "))));
                        case 4 -> io.println(printer.films(filmService.findByGenre(io.readCode("Код жанра: "))));
                        case 5 -> {
                            filmService.delete(io.readCode("Код фильма: "));
                            io.println("Фильм удалён.");
                        }
                        case 6 -> io.println(printer.genres(genreService.findAll()));
                        case 7 -> {
                            genreService.create(io.readCode("Код жанра: "), io.readLine("Название: "));
                            io.println("Жанр добавлен.");
                        }
                        case 8 -> {
                            genreService.delete(io.readCode("Код жанра для удаления: "));
                            io.println("Жанр удалён.");
                        }
                    }
                });
    }

    private void sessionsMenu() {
        runMenu("Сеансы", List.of("Показать все", "Показать по залу", "Добавить", "Удалить"), "Назад", choice -> {
            switch (choice) {
                case 1 -> io.println(printer.sessions(sessionService.findAll()));
                case 2 -> io.println(printer.sessions(sessionService.findByHall(io.readCode("Код зала: "))));
                case 3 -> {
                    sessionService.create(io.readCode("Код зала: "), io.readCode("Код фильма: "),
                            io.readDateTime("Начало (дд.мм.гггг чч:мм): "), io.readPrice("Цена билета: "));
                    io.println("Сеанс добавлен, билеты на все места созданы.");
                }
                case 4 -> {
                    sessionService.delete(io.readCode("Код сеанса: "));
                    io.println("Сеанс удалён.");
                }
            }
        });
    }

    private void usersMenu() {
        runMenu("Пользователи", List.of("Показать", "Зарегистрировать", "Удалить"), "Назад", choice -> {
            switch (choice) {
                case 1 -> io.println(printer.users(userService.findAll()));
                case 2 -> {
                    UserRole role = UserRole.values()[io.readInt("Роль (1 - администратор, 2 - менеджер, 3 - покупатель): ", 1, 3) - 1];
                    userService.register(role, io.readLogin("Логин: "), io.readLine("Пароль: "),
                            io.readLine("Фамилия: "), io.readLine("Имя: "), io.readLine("Email: "));
                    io.println("Пользователь зарегистрирован.");
                }
                case 3 -> {
                    userService.delete(io.readLogin("Логин: "));
                    io.println("Пользователь удалён.");
                }
            }
        });
    }

    private void ticketsMenu() {
        runMenu("Билеты", List.of("Свободные места сеанса", "Купить билет", "Мои билеты"), "Назад", choice -> {
            switch (choice) {
                case 1 -> io.println(printer.freeSeats(ticketService.findAvailable(io.readCode("Код сеанса: "))));
                case 2 -> buyTicket();
                case 3 -> io.println(printer.customerTickets(ticketService.findByCustomer(io.readLogin("Логин: "))));
            }
        });
    }

    private void buyTicket() {
        String sessionCode = io.readCode("Код сеанса: ");
        int rowNum = io.readInt("Ряд: ");
        int seatNum = io.readInt("Место: ");
        String login = io.readLogin("Логин покупателя: ");
        Ticket ticket = ticketService.findBySeat(sessionCode, rowNum, seatNum).orElseThrow(
                () -> new IllegalArgumentException("На сеансе " + sessionCode + " нет такого места"));
        ticketService.buy(ticket.getTicketNumber(), login);
        io.println("Билет " + ticket.getTicketNumber() + " куплен.");
    }

    private void reportsMenu() {
        runMenu("Отчёты и аналитика", List.of("Выручка по сеансу", "Популярность фильмов", "Общая статистика"), "Назад", choice -> {
            switch (choice) {
                case 1 -> {
                    String sessionCode = io.readCode("Код сеанса: ");
                    io.println("Выручка сеанса " + sessionCode + ": " + reportService.sessionRevenue(sessionCode));
                }
                case 2 -> io.println(printer.filmSales(reportService.soldTicketsByFilm()));
                case 3 -> io.println(printer.statistics(reportService.statistics()));
            }
        });
    }

    private void runCashiers() {
        String sessionCode = io.readCode("Код сеанса: ");
        int requestsCount = io.readInt("Число заявок (1-" + MAX_REQUESTS + "): ", 1, MAX_REQUESTS);
        int cashiers = io.readInt("Число кассиров (1-" + MAX_CASHIERS + "): ", 1, MAX_CASHIERS);
        Queue<PurchaseRequest> queue = PurchaseRequest.queueOf(ticketService.findAvailable(sessionCode),
                userService.findByRole(UserRole.CUSTOMER), requestsCount);
        io.println("Обработка очереди кассирами:");
        int sold = purchaseProcessor.process(queue, cashiers);
        io.println("Продано билетов: " + sold + ", отклонено заявок: " + (requestsCount - sold));
    }
}