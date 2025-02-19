package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    // Добавление пользователя
    @PostMapping
    public User creatUser(@Valid @RequestBody User user) {
        log.info("Метод: {}. Новый пользователь: {}", getMethod(), user);
        checkUser(user);
        checkUserName(user);

        user.setId(getNextId());
        users.put(user.getId(), user);

        return user;
    }

    // Обновление пользователя
    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        log.info("Метод: {}. Пользователь для обновления: {}", getMethod(), newUser);
        if (!users.containsKey(newUser.getId())) {
            String message = "Пользователь с ID: " + newUser.getId() + " — не найден.";
            log.error(message);
            throw new ValidationException(message);
        }

        checkUser(newUser);
        checkUserName(newUser);
        users.put(newUser.getId(), newUser);

        return newUser;
    }

    // Получение всех пользователей
    @GetMapping
    public Collection<User> getAllFilms() {
        return users.values();
    }

/*
    ------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*/

    // Создание нового ID
    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }

    // Проверка пользователя
    private void checkUser(User user) {
        if (user.getLogin().indexOf(" ") > 0) {
            String message = "Логин: " + user.getLogin() + " - не может содержать пробелы";
            log.error(message);
            throw new ValidationException(message);
        }
    }

    // Устанавливает в качестве имени логин, если имя пустое
    private void checkUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Метод: {}. В качестве имени использован логин: {}",
                    getMethod(), user.getLogin());
        }
    }

    // Возвращает имя метода для логирования
    private String getMethod() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }
}
