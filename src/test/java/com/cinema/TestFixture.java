package com.cinema;

import com.cinema.concurrent.ParallelPurchaseProcessor;
import com.cinema.model.Film;
import com.cinema.model.FilmSession;
import com.cinema.model.Genre;
import com.cinema.model.Hall;
import com.cinema.model.Ticket;
import com.cinema.model.User;
import com.cinema.model.UserRole;
import com.cinema.repository.EntityRepository;
import com.cinema.repository.RepositoryConfig;
import com.cinema.service.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Создаёт сервисы и репозитории для тестов.
 * Каждый тест получает пустые хранилища, чтобы не зависеть от других тестов.
 */
public class TestFixture {
    public static final LocalDateTime EVENING = LocalDateTime.of(2026, 10, 5, 18, 0);
    public static final String SESSION_CODE = "H1-20261005-1800";

    private final RepositoryConfig config = new RepositoryConfig();

    public final EntityRepository<Hall, String> halls = config.hallRepository();
    public final EntityRepository<Genre, String> genres = config.genreRepository();
    public final EntityRepository<Film, String> films = config.filmRepository();
    public final EntityRepository<FilmSession, String> sessions = config.sessionRepository();
    public final EntityRepository<Ticket, String> tickets = config.ticketRepository();
    public final EntityRepository<User, String> users = config.userRepository();

    public final HallService hallService = new HallServiceImpl(halls, sessions);

    public final GenreService genreService = new GenreServiceImpl(genres, films);

    public final FilmService filmService = new FilmServiceImpl(films, genres, sessions);
    public final SessionService sessionService = new SessionServiceImpl(halls, films, sessions, tickets);
    public final TicketService ticketService = new TicketServiceImpl(tickets, users);
    public final UserService userService = new UserServiceImpl(users, tickets);
    public final ReportService reportService = new ReportServiceImpl(halls, films, sessions, tickets);
    public final ParallelPurchaseProcessor purchaseProcessor = new ParallelPurchaseProcessor(ticketService);

    /** Зал H1 (2 ряда по 3 места = 6 мест), жанр DRAMA, фильм FLM-1 и сеанс с ценой 300. */
    public void createSession() {
        hallService.create("H1", "Hall 1", 2, 3);
        genreService.create("DRAMA", "Драма");
        filmService.create("FLM-1", "Test Film", 100, "12+", Set.of("DRAMA"));
        sessionService.create("H1", "FLM-1", EVENING, new BigDecimal("300.00"));
    }

    public void createCustomer(String login) {
        userService.register(UserRole.CUSTOMER, login, "pass123", "Иванов", "Иван", login + "@mail.test");
    }
}