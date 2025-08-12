package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        log.info("Метод: {}. Новый пользователь: {}", getMethod(), user);
        validate(user);

        return userStorage.create(user);
    }

    public User update(User newUser) {
        log.info("Метод: {}. Пользователь для обновления: {}", getMethod(), newUser);
        validate(newUser);

        return userStorage.update(newUser);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }


    // Добавление в друзья
    public void addFriend(long id, long friendId) {
        log.info("Метод: {}. ID пользователя: {} ИД друга: {}", getMethod(), id, friendId);
        checkUser(id);
        checkUser(friendId);

        userStorage.addFriends(id, friendId);
    }

    // Удаление из друзей
    public void deleteFriend(long id, long friendId) {
        checkUser(id);
        checkUser(friendId);

        userStorage.deleteFriend(id, friendId);
    }

    // Получение списка друзей
    public List<User> getAllFriends(long id) {
        checkUser(id);
        return userStorage.getFriends(id);
    }

    // Список друзей, общих с другим пользователем
    public Collection<User> getCommonFriend(long id, long otherId) {
        checkUser(id);
        checkUser(otherId);

        return userStorage.getCommonFriend(id, otherId);
    }


    /*
   ------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*/
    // Проверка существования пользователей
    private void checkUser(long id) {
        if (!userStorage.existsById(id)) {
            String message = "Пользователь с ID: " + id + " — не найден.";
            log.error(message);
            throw new NotFoundException(message);
        }
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

    // Удаление всех пользователей из БД для тестирования приложения
    public void clearUsers() {
        userStorage.clearUsers();
    }
}
