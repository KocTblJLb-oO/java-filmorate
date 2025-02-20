package ru.yandex.practicum.filmorate.controller;

import com.google.gson.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.other.LocalDateAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    Film film;
    ConfigurableApplicationContext context;
    Gson gson;

    @BeforeEach
    public void startTest() {
        film = new Film(1, "testName", "descriptionTest", LocalDate.of(1989, 12, 23), 100);
        context = SpringApplication.run(FilmorateApplication.class);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    @AfterEach
    public void stopTest() {
        SpringApplication.exit(context, () -> 0);
    }

    // Добавление фильма
    @Test
    public void addFilm() throws IOException, InterruptedException {
        String filmJson = gson.toJson(film);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/films");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа сервера не совпадает");

        // Запрос фильма и проверка его названия
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        assertEquals("testName", jsonObject.get("name").getAsString(), "Не удалось получить фильм");
    }

    // Добавление фильма старше 28 декабря 1895 года
    @Test
    public void addOldFilm() throws IOException, InterruptedException {
        film.setReleaseDate(LocalDate.of(1800, 1, 1));

        String filmJson = gson.toJson(film);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/films");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode(), "Код ответа сервера не совпадает");
    }
}