package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    // Добавление пользователя
    User create(User user);

    // Обновление пользователя
    User update(User newUser);

    // Получение всех пользователей
    Collection<User> getAllUsers();

    // Получение пользователя по ИД
    User getUserById(long id);

    // Проверка существования пользователя
    boolean existsById(long id);

    void clearUsers();

    //Добавление в друзья
    void addFriends(long id, long idFriends);

    List<User> getFriends(long id);

    void deleteFriend(long id, long friendId);

    Collection<User> getCommonFriend(long id, long otherId);
}
