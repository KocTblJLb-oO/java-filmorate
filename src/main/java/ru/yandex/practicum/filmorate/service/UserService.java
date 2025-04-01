package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    // Добавление в друзья
    public void addFriend(long id, long friendId) {
        checkUser(id);
        checkUser(friendId);

        userStorage.getUserById(id).getFriends().add(friendId);
        userStorage.getUserById(friendId).getFriends().add(id);
    }

    // Удаление из друзей
    public void deleteFriend(long id, long friendId) {
        checkUser(id);
        checkUser(friendId);

        userStorage.getUserById(id).getFriends().remove(friendId);
        userStorage.getUserById(friendId).getFriends().remove(id);
    }

    // Получение списка друзей
    public List<User> getAllFriends(long id) {
        checkUser(id);

        Set<Long> userFriends = userStorage.getUserById(id).getFriends();

        return userStorage.getAllUsers()
                .stream()
                .filter(user -> userFriends.contains(user.getId()))
                .collect(Collectors.toList());
    }

    // Список друзей, общих с другим пользователем
    public Collection<User> getCommonFriend(long id, long otherId) {
        checkUser(id);
        checkUser(otherId);

        Set<Long> commonFriends = userStorage.getUserById(id).getFriends(); // Список id друзей пользователя
        commonFriends.retainAll(userStorage.getUserById(otherId).getFriends()); // В списке остаются только совпадающие id

        return userStorage.getAllUsers()
                .stream()
                .filter(user -> commonFriends.contains(user.getId()))
                .collect(Collectors.toList());
    }

    /*
   ------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*/
    // Проверка пользователей
    private void checkUser(long id) {
        if (!userStorage.findUser(id)) {
            String message = "Пользователь с ID: " + id + " — не найден.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }
}
