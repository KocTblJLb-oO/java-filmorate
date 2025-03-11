package ru.yandex.practicum.filmorate.controller;

import com.google.gson.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.other.LocalDateAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {
    User user;
    ConfigurableApplicationContext context;
    Gson gson;

    @BeforeEach
    public void startTest() {
        user = new User(1, "aaa@aaa.aa", "loginTest", "nameTest", LocalDate.of(1989, 12, 23), new HashSet<>());
        context = SpringApplication.run(FilmorateApplication.class);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    @AfterEach
    public void stopTest() {
        SpringApplication.exit(context, () -> 0);
    }

    // Добавление пользователя
    @Test
    public void addUser() throws IOException, InterruptedException {
        String userJson = gson.toJson(user);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/users");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа сервера не совпадает");

        // Сверяем возвращённого и отправленного пользователя по логину
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        assertEquals("loginTest", jsonObject.get("login").getAsString(), "Не удалось получить пользователя");
    }

    // Добавление пользователя без имени
    @Test
    public void addUserWithoutName() throws IOException, InterruptedException {
        user.setName(null);

        String userJson = gson.toJson(user);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/users");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа сервера не совпадает");

        // Проверка имени возвращённого пользователя
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        assertEquals("loginTest", jsonObject.get("name").getAsString(), "Не удалось получить пользователя");
    }
}