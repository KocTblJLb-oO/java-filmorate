package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
        if (!userStorage.findUser(id)) {
            String message = "Пользователь с ID: " + id + " — не найден.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    // Возвращает имя метода для логирования
    private String getMethod() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }
}
