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

    private final Map<Long, User> users = new HashMap<>();
    private long currentMaxId = 0L;

    // Добавление пользователя
    @PostMapping
    public User creatUser(@Valid @RequestBody User user) {
        log.info("Метод: {}. Новый пользователь: {}", getMethod(), user);
        validate(user);

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

        validate(newUser);
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
    private long getNextId() {
        return ++currentMaxId;
    }

    // Проверка пользователя
    private void validate(User user) {
        if (user.getLogin().indexOf(" ") > 0) {
            String message = "Логин: " + user.getLogin() + " - не может содержать пробелы";
            log.error(message);
            throw new ValidationException(message);
        }
        // Устанавливает в качестве имени логин, если имя пустое
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
