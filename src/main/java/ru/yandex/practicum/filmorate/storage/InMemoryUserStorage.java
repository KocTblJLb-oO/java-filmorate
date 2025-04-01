package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long currentMaxId = 0L;

    // Добавление пользователя
    @Override
    public User create(User user) {
        log.info("Метод: {}. Новый пользователь: {}", getMethod(), user);
        validate(user);

        user.setId(getNextId());
        users.put(user.getId(), user);

        return user;
    }

    // Обновление пользователя
    @Override
    public User update(User newUser) {
        log.info("Метод: {}. Пользователь для обновления: {}", getMethod(), newUser);
        if (!users.containsKey(newUser.getId())) {
            String message = "Пользователь с ID: " + newUser.getId() + " — не найден.";
            log.error(message);
            throw new NotFoundException(message);
        }

        validate(newUser);
        users.put(newUser.getId(), newUser);

        return newUser;
    }

    // Получение всех пользователей
    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    // Получение пользователя по ИД
    @Override
    public User getUserById(long id) {
        return users.get(id);
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
        // Добавляем список друзей, если в запросе у пользователя его нет
        if (user.getFriends() == null) {
            log.trace("Метод: {}. Друзья: {}", getMethod(), user.getFriends());
            user.setFriends(new HashSet<>());
        }
    }

    /*
    ------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*/

    // Возвращает имя метода для логирования
    private String getMethod() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }

    // Проверка существования пользователя
    public boolean findUser(long id) {
        return users.containsKey(id);
    }
}
