package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;

import java.util.*;

@Repository("UserDbStorage")
@RequiredArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;
    private long currentMaxId = 0L;

    @Override
    public User create(User user) {
        log.info("Метод: {}. Новый пользователь: {}", getMethod(), user);
        user.setId(getNextId());

        // Сначала пытаемся обновить пользователя на случай, если он уже есть
        String updateQuery = "UPDATE USERS " +
                "SET email = ?, login = ?, name = ?, BIRTH_DAY = ? " +
                "WHERE user_id = ?";
        int count = jdbc.update(updateQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());

        log.info("Метод: {}. Обновлено строк: {}", getMethod(), count);

        if (count == 0) {
            // Если ни одной строки не обновилось, создаём пользователя
            String query = "INSERT INTO USERS (user_id, email, login, name, BIRTH_DAY) " +
                    "VALUES " +
                    "(?, ?, ?, ?, ?)";
            jdbc.update(query, user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        }
        return jdbc.queryForObject("SELECT * FROM USERS where user_id = ?;", mapper, user.getId());
    }

    //Обновление пользователя
    @Override
    public User update(User newUser) {
        log.info("Метод: {}. Пользователь для обновления: {}", getMethod(), newUser);

        String updateQuery = "UPDATE USERS " +
                "SET email = ?, login = ?, name = ?, BIRTH_DAY = ? " +
                "WHERE user_id = ?";
        int count = jdbc.update(updateQuery, newUser.getEmail(), newUser.getLogin(), newUser.getName(), newUser.getBirthday(), newUser.getId());

        if (count == 0) {
            // Если ни одной строки не обновилось, возвращаем ошибку
            String message = "Пользователь с ID: " + newUser.getId() + " — не найден.";
            log.error(message);
            throw new NotFoundException(message);
        }

        return jdbc.queryForObject("SELECT * FROM USERS where user_id = ?;", mapper, newUser.getId());
    }

    @Override
    public Collection<User> getAllUsers() {
        String query = "SELECT * FROM USERS";
        return jdbc.query(query, mapper);
    }

    @Override
    public User getUserById(long id) {
        String query =
                "SELECT * FROM USERS " +
                        "where user_id = ?";
        return jdbc.queryForObject(query, mapper, id);
    }

/*
    ------------------------------------------------ ДРУЗЬЯ
*/

    // Добавление в друзья
    public void addFriends(long id, long idFriend) {
        log.info("Метод: {}. Пользователь: {}, Друг: {}", getMethod(), id, idFriend);

        String query = "INSERT INTO Friends (user1_id, user2_id, status) " +
                "VALUES " +
                "(?, ?, ?)";
        jdbc.update(query, id, idFriend, "false");
    }

    //Список друзей пользователя
    public List<User> getFriends(long id) {
        log.info("Метод: {}. Пользователь: {}", getMethod(), id);

        List<Long> friendIds;
        String query = "SELECT user2_id FROM FRIENDS WHERE user1_id = ?";
        friendIds = jdbc.queryForList(query, Long.class, id);
        if (friendIds.isEmpty()) {
            log.info("Метод: {}. Друзей нет", getMethod());// Если друзей нет возвращаем пустой список
            return Collections.emptyList();
        }

        String placeholders = String.join(",", Collections.nCopies(friendIds.size(), "?"));
        String queryFriends = "SELECT * FROM USERS WHERE user_id IN (" + placeholders + ")";
        return jdbc.query(queryFriends, mapper, friendIds.toArray());
    }

    // Удаление из друзей
    public void deleteFriend(long id, long friendId) {
        log.info("Метод: {}. Пользователь: {}, Друг: {}", getMethod(), id, friendId);

        String queryDeteteFriends = "DELETE FROM FRIENDS " +
                "Where user1_id = ? and user2_id = ?;";
        jdbc.update(queryDeteteFriends, id, friendId);
    }

    @Override
    public Collection<User> getCommonFriend(long id, long otherId) {
        log.info("Метод: {}. Пользователь: {}, С кем общие друзья: {}", getMethod(), id, otherId);

        List<Long> friendIds;
        String query = "SELECT user2_id FROM FRIENDS " +
                "WHERE user1_id = ? " +
                "and user2_id in (" +
                "SELECT user2_id FROM FRIENDS " +
                "WHERE user1_id = ?)";
        friendIds = jdbc.queryForList(query, Long.class, id, otherId);
        if (friendIds.isEmpty()) {
            log.info("Метод: {}. Друзей нет", getMethod());// Если друзей нет возвращаем пустой список
            return Collections.emptyList();
        }

        String placeholders = String.join(",", Collections.nCopies(friendIds.size(), "?"));
        String queryFriends = "SELECT * FROM USERS WHERE user_id IN (" + placeholders + ")";
        return jdbc.query(queryFriends, mapper, friendIds.toArray());
    }

    /*
    ------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*/
    // Создание нового ID
    private long getNextId() {
        return ++currentMaxId;
    }

    // Возвращает имя метода для логирования
    private String getMethod() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }

    public void clearUsers() {
        String queryDeteteFriends = "DELETE FROM FRIENDS;";
        String updateQuery = "DELETE FROM USERS;";
        try {
            int countFriends = jdbc.update(queryDeteteFriends);
            int countUsers = jdbc.update(updateQuery);

            log.trace("Метод: {}. Удалено друзей: {}", getMethod(), countFriends);
            log.trace("Метод: {}. Удалено пользователей: {}", getMethod(), countUsers);
        } catch (DataAccessException e) {
            e.getMessage();
        }
    }

    // Проверка существования пользователя
    public boolean existsById(long id) {
        log.info("Метод: {}. ID пользователя: {} ", getMethod(), id);
        String query = "SELECT COUNT(user_id) FROM USERS where user_id = ?;";
        int result = jdbc.queryForObject(query, Integer.class, id);

        return result == 1;
    }
}
