package com.cinema.cli;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

@Component
public class ConsoleIO {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final Scanner scanner = new Scanner(System.in,
            System.getProperty("stdin.encoding", Charset.defaultCharset().name()));

    public void println(String text) {
        System.out.println(text);
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public String readCode(String prompt) {
        return readLine(prompt).toUpperCase(Locale.ROOT);
    }

    public String readLogin(String prompt) {
        return readLine(prompt).toLowerCase(Locale.ROOT);
    }

    public Set<String> readCodes(String prompt) {
        Set<String> codes = new HashSet<>();
        for (String part : readLine(prompt).split(",")) {
            if (!part.isBlank()) {
                codes.add(part.trim().toUpperCase(Locale.ROOT));
            }
        }
        return codes;
    }

    public int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLine(prompt));
            } catch (NumberFormatException e) {
                println("Введите целое число");
            }
        }
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            println("Введите число от " + min + " до " + max);
        }
    }

    public BigDecimal readPrice(String prompt) {
        while (true) {
            try {
                return new BigDecimal(readLine(prompt).replace(',', '.'));
            } catch (NumberFormatException e) {
                println("Введите число, например 500 или 350.50");
            }
        }
    }

    public LocalDateTime readDateTime(String prompt) {
        while (true) {
            try {
                return LocalDateTime.parse(readLine(prompt), DATE_TIME);
            } catch (DateTimeParseException e) {
                println("Введите дату и время в формате дд.мм.гггг чч:мм, например 05.10.2026 18:00");
            }
        }
    }
}
