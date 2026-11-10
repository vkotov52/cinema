package com.cinema.repository;

import com.cinema.model.Film;
import com.cinema.model.FilmSession;
import com.cinema.model.Genre;
import com.cinema.model.Hall;
import com.cinema.model.Ticket;
import com.cinema.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Создаёт по одному хранилищу в памяти для каждой сущности. */
@Configuration
public class RepositoryConfig {

    @Bean
    public EntityRepository<Hall, String> hallRepository() {
        return new InMemoryRepository<>(Hall::getHallCode);
    }

    @Bean
    public EntityRepository<Genre, String> genreRepository() {
        return new InMemoryRepository<>(Genre::getGenreCode);
    }

    @Bean
    public EntityRepository<Film, String> filmRepository() {
        return new InMemoryRepository<>(Film::getFilmCode);
    }

    @Bean
    public EntityRepository<FilmSession, String> sessionRepository() {
        return new InMemoryRepository<>(FilmSession::getSessionCode);
    }

    @Bean
    public EntityRepository<Ticket, String> ticketRepository() {
        return new InMemoryRepository<>(Ticket::getTicketNumber);
    }

    @Bean
    public EntityRepository<User, String> userRepository() {
        return new InMemoryRepository<>(User::getLogin);
    }
}
