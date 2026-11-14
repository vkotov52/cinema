package com.cinema.controller;

import com.cinema.model.Film;
import com.cinema.model.Genre;
import com.cinema.service.FilmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.NoSuchElementException;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Тесты для FilmController, проверяющие обработку HTTP-запросов и работу глобального обработчика исключений через MockMvc. */
@SpringBootTest(classes = {FilmController.class, ApiExceptionHandler.class})
class FilmControllerTest {

    @MockitoBean
    private FilmService service;

    @Autowired
    private FilmController controller;

    @Autowired
    private ApiExceptionHandler exceptionHandler;

    private MockMvc mockMvc;

    /** Тестовые данные фильма, переиспользуемые в сценариях проверки. */
    private final Film film = new Film("F1", "Тест", 100, "12+", Set.of(new Genre("DRAMA", "Драма")));

    /** Инициализирует MockMvc, подключая контроллер и обработчик исключений для изолированного тестирования веб-слоя. */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(exceptionHandler)
                .build();
    }

    /** Проверяет успешный GET-запрос: возвращает HTTP 200 и корректные данные фильма в формате JSON. */
    @Test
    void returnsFilmByCode() throws Exception {
        when(service.findById("F1")).thenReturn(film);

        mockMvc.perform(get("/api/films/F1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filmCode").value("F1"))
                .andExpect(jsonPath("$.title").value("Тест"));
    }

    /** Проверяет, что запрос несуществующего фильма возвращает HTTP 404 и корректное сообщение об ошибке от обработчика исключений. */
    @Test
    void returns404WhenFilmDoesNotExist() throws Exception {
        when(service.findById("F9")).thenThrow(new NoSuchElementException("Фильм не найден: F9"));

        mockMvc.perform(get("/api/films/F9"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Фильм не найден: F9"));
    }

    /** Проверяет успешный POST-запрос: валидный JSON-запрос создаёт фильм и возвращает HTTP 201 Created. */
    @Test
    void createsFilmFromValidRequest() throws Exception {
        when(service.create(eq("F1"), eq("Тест"), anyInt(), eq("12+"), anySet())).thenReturn(film);

        mockMvc.perform(post("/api/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"F1","title":"Тест","durationMin":100,
                                 "ageRating":"12+","genreCodes":["DRAMA"]}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.filmCode").value("F1"));
    }

    /** Проверяет работу валидации: запрос с пустыми или некорректными полями отклоняется с кодом HTTP 400 Bad Request. */
    @Test
    void rejectsInvalidRequestBody() throws Exception {
        mockMvc.perform(post("/api/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"","title":"","durationMin":0,"ageRating":"","genreCodes":[]}
                                """))
                .andExpect(status().isBadRequest());
    }
}