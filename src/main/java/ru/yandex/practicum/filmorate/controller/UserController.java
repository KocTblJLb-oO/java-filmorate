package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    /*
        ------------------------------------------------ Работа с пользователями
    */

    // Добавление пользователя
    @PostMapping
    public User creatUser(@Valid @RequestBody User user) {
        log.info("Метод: {}. Новый пользователь: {}", getMethod(), user);
        return userStorage.create(user);
    }

    // Обновление пользователя
    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        log.info("Метод: {}. Пользователь для обновления: {}", getMethod(), newUser);
        return userStorage.update(newUser);
    }

    // Получение всех пользователей
    @GetMapping
    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    /*
    ------------------------------------------------ Работа с друзьями
*/

    // Добавление в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") long id, @PathVariable("friendId") long friendId) {
        log.info("Метод: {}. ID пользователя: {} ИД друга: {}", getMethod(), id, friendId);
        userService.addFriend(id, friendId);
    }

    // Удаление из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") long id, @PathVariable("friendId") long friendId) {
        userService.deleteFriend(id, friendId);
    }

    // Получение списка друзей
    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable long id) {
        return userService.getAllFriends(id);
    }

    // Список друзей, общих с другим пользователем
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriend(
            @PathVariable("id") long id,
            @PathVariable("otherId") long otherId) {
        return userService.getCommonFriend(id, otherId);
    }



/*
    ------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*/

    // Возвращает имя метода для логирования
    private String getMethod() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }
}
