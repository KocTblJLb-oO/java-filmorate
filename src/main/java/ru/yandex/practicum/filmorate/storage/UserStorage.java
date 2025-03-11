package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

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
    boolean findUser(long id);
}
