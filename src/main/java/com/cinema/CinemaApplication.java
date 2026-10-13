package com.cinema;

import com.cinema.cli.ConsoleApp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CinemaApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CinemaApplication.class, args);
        context.getBean(ConsoleApp.class).run();
        context.close();
    }
}
