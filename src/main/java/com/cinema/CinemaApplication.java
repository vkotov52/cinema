package com.cinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Точка входа в приложение. */
@SpringBootApplication
public class CinemaApplication {

    /** Главный метод приложения. Запускает Spring Boot и ждёт HTTP-запросов на порту 8080. */
    public static void main(String[] args) {
        SpringApplication.run(CinemaApplication.class, args);
    }
}