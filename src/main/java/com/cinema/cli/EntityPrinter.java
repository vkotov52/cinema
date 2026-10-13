package com.cinema.cli;

import com.cinema.model.Film;
import com.cinema.model.FilmSession;
import com.cinema.model.Genre;
import com.cinema.model.Hall;
import com.cinema.model.Ticket;
import com.cinema.model.User;
import com.cinema.service.Statistics;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** Превращает объекты в таблицы для вывода на экран. */
@Component
public class EntityPrinter {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final TablePrinter tablePrinter;

    public EntityPrinter(TablePrinter tablePrinter) {
        this.tablePrinter = tablePrinter;
    }

    public String halls(List<Hall> halls) {
        List<List<String>> rows = new ArrayList<>();
        for (Hall hall : halls) {
            rows.add(List.of(hall.getHallCode(), hall.getName(), String.valueOf(hall.getRowsCount()),
                    String.valueOf(hall.getSeatsPerRow()), String.valueOf(hall.capacity())));
        }
        return tablePrinter.render("Залы (" + halls.size() + ")",
                List.of("Код", "Название", "Рядов", "Мест в ряду", "Вместимость"), rows);
    }

    public String genres(List<Genre> genres) {
        List<List<String>> rows = new ArrayList<>();
        for (Genre genre : genres) {
            rows.add(List.of(genre.getGenreCode(), genre.getName()));
        }
        return tablePrinter.render("Жанры (" + genres.size() + ")", List.of("Код", "Название"), rows);
    }

    public String films(List<Film> films) {
        List<List<String>> rows = new ArrayList<>();
        for (Film film : films) {
            String genreNames = film.getGenres().stream().map(Genre::getName).sorted().collect(Collectors.joining(", "));
            rows.add(List.of(film.getFilmCode(), film.getTitle(), String.valueOf(film.getDurationMin()),
                    film.getAgeRating(), genreNames));
        }
        return tablePrinter.render("Фильмы (" + films.size() + ")",
                List.of("Код", "Название", "Длит., мин", "Возраст", "Жанры"), rows);
    }

    public String sessions(List<FilmSession> sessions) {
        List<List<String>> rows = new ArrayList<>();
        for (FilmSession session : sessions) {
            rows.add(List.of(session.getSessionCode(), session.getHall().getHallCode(), session.getFilm().getTitle(),
                    session.getStartTime().format(DATE_TIME), session.getBasePrice().toPlainString()));
        }
        return tablePrinter.render("Сеансы (" + sessions.size() + ")",
                List.of("Код", "Зал", "Фильм", "Начало", "Цена"), rows);
    }

    public String users(List<User> users) {
        List<List<String>> rows = new ArrayList<>();
        for (User user : users) {
            rows.add(List.of(user.getLogin(), user.getLastName(), user.getFirstName(), user.getEmail(),
                    user.getRole().name()));
        }
        return tablePrinter.render("Пользователи (" + users.size() + ")",
                List.of("Логин", "Фамилия", "Имя", "Email", "Роль"), rows);
    }

    /** Свободные места: по строке на ряд, в ней номера свободных мест. */
    public String freeSeats(List<Ticket> tickets) {
        Map<Integer, List<String>> seatsByRow = new TreeMap<>();
        for (Ticket ticket : tickets) {
            seatsByRow.computeIfAbsent(ticket.getRowNum(), row -> new ArrayList<>())
                    .add(String.valueOf(ticket.getSeatNum()));
        }
        List<List<String>> rows = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> entry : seatsByRow.entrySet()) {
            rows.add(List.of(String.valueOf(entry.getKey()), String.join(" ", entry.getValue())));
        }
        return tablePrinter.render("Свободные места: " + tickets.size(), List.of("Ряд", "Свободные места"), rows);
    }

    public String customerTickets(List<Ticket> tickets) {
        List<List<String>> rows = new ArrayList<>();
        for (Ticket ticket : tickets) {
            rows.add(List.of(ticket.getTicketNumber(), ticket.getSessionCode(), String.valueOf(ticket.getRowNum()),
                    String.valueOf(ticket.getSeatNum()), ticket.getPrice().toPlainString(),
                    ticket.getSoldAt().format(DATE_TIME)));
        }
        return tablePrinter.render("Билеты пользователя (" + tickets.size() + ")",
                List.of("Номер", "Сеанс", "Ряд", "Место", "Цена", "Продан"), rows);
    }

    public String filmSales(Map<String, Long> sales) {
        List<List<String>> rows = new ArrayList<>();
        for (Map.Entry<String, Long> entry : sales.entrySet()) {
            rows.add(List.of(entry.getKey(), String.valueOf(entry.getValue())));
        }
        return tablePrinter.render("Популярность фильмов", List.of("Фильм", "Продано билетов"), rows);
    }

    public String statistics(Statistics statistics) {
        List<List<String>> rows = List.of(
                List.of("Залов", String.valueOf(statistics.halls())),
                List.of("Фильмов", String.valueOf(statistics.films())),
                List.of("Сеансов", String.valueOf(statistics.sessions())),
                List.of("Проданных билетов", String.valueOf(statistics.soldTickets())));
        return tablePrinter.render("Общая статистика", List.of("Показатель", "Значение"), rows);
    }
}
