package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmsRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.util.Collection;

@Repository("FilmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmsRowMapper mapper;
    private long currentMaxId = 0L;
    private final UserStorage userStorage;

    public FilmDbStorage(JdbcTemplate jdbc, FilmsRowMapper mapper, @Qualifier("UserDbStorage") UserStorage userStorage) {
        this.jdbc = jdbc;
        this.mapper = mapper;
        this.userStorage = userStorage;
    }

    @Override
    public Film create(Film film) {
        log.info("Метод: {}. Новый фильм: {}", getMethod(), film);
        validate(film);
        film.setId(getNextId());

        // Сначала пытаемся обновить фильм на случай, если он уже есть
        String updateQuery = "UPDATE FILM " +
                "SET name = ?, description = ?, RELEASE_DATE = ?, duration = ? , mpa_id = ? " +
                "WHERE film_id = ?";
        int count = jdbc.update(updateQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());

        log.info("Метод: {}. Обновлено строк: {}", getMethod(), count);

        if (count == 0) {
            // Если ни одной строки не обновилось, создаём фильм
            try {
                String query = "INSERT INTO FILM (film_id, name, description, RELEASE_DATE, duration, mpa_id) " +
                        "VALUES " +
                        "(?, ?, ?, ?, ?, ?)";
                jdbc.update(query, film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                        film.getDuration(), film.getMpa().getId());
            } catch (DataAccessException e) {
                System.out.println("Ошибка вставки значения: " + e.getMessage());
            }
        }
        updateGenre(film);
        return jdbc.queryForObject("SELECT * FROM FILM where film_id = ?;", mapper, film.getId());
    }

    // Обновление жанра
    public void updateGenre(Film film) {
        log.info("Метод: {}. Обновление жанра. Жанры: {}", getMethod(), film.getGenres());
        String deteteGenre = "DELETE FROM FILM_GENRES " +
                "Where film_id = ?"; // Удаляем старые жанры перед вставкой новых
        jdbc.update(deteteGenre, film.getId());
        for (Genre genre : film.getGenres()) {
            try {
                String query = "INSERT INTO FILM_GENRES (film_id, genre_id) " +
                        "VALUES " +
                        "(?, ?)";
                jdbc.update(query, film.getId(), genre.getId());
            } catch (DataAccessException e) {
                System.out.println("Ошибка вставки значения. Такой жанр уже есть: " + e.getMessage());
            }
        }

    }

    @Override
    public Film update(Film newFilm) {
        log.info("Метод: {}. Фильм для обновления: {}", getMethod(), newFilm);
        checkFilm(newFilm.getId());
        validate(newFilm);

        try {
            String updateQuery = "UPDATE FILM " +
                    "SET name = ?, description = ?, RELEASE_DATE = ?, duration = ? , mpa_id = ? " +
                    "WHERE film_id = ?";
            jdbc.update(updateQuery, newFilm.getName(), newFilm.getDescription(), newFilm.getReleaseDate(),
                    newFilm.getDuration(), newFilm.getMpa().getId(), newFilm.getId());
        } catch (DataAccessException e) {
            System.out.println("Ошибка вставки значения: " + e.getMessage());
        }

        updateGenre(newFilm);
        log.info("Метод: {}. Фильм обновлён", getMethod());
        return jdbc.queryForObject("SELECT * FROM FILM where film_id = ?;", mapper, newFilm.getId());
    }

    @Override
    public Collection<Film> getAllFilms() {
        String query = "SELECT * FROM FILM";
        return jdbc.query(query, mapper);
    }

    @Override
    public Film getFilm(long id) {
        String query =
                "SELECT * FROM FILM " +
                        "where film_id = ?";
        return jdbc.queryForObject(query, mapper, id);
    }

    public Collection<Film> getPopular(long count) {
        String query = "SELECT f.*, COUNT(l.user_id) as likes " +
                "FROM FILM f " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.user_id) DESC " +
                "LIMIT ?";
        return jdbc.query(query, mapper, count);
    }

    // Лайк фильму
    public void addLike(long id, long userId) {
        log.info("Метод: {}. ID фильма: {} ИД пользователя: {}", getMethod(), id, userId);
        checkUser(userId);
        checkFilm(id);

        try {
            String query = "INSERT INTO likes (film_id, user_id) " +
                    "VALUES " +
                    "(?, ?)";
            jdbc.update(query, id, userId);
        } catch (DataAccessException e) {
            System.out.println("Ошибка вставки значения. Такой лайк уже есть: " + e.getMessage());
        }
    }

        /*
    ------------------------------------------------ ФИЛЬМЫ ПО ЖАНРАМ
*/

    public Collection<Film> getFilmsWithGenre(long genreId) {
        String query = "SELECT * FROM FILM " +
                "WHERE film_id in (" +
                "SELECT film_id FROM FILM_GENRES " +
                "WHERE genre_id = ?)";

        return jdbc.query(query, mapper, genreId);
    }

    /*
    ------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*/

    // Создание нового ID
    private long getNextId() {
        return ++currentMaxId;
    }

    // Проверка фильма на корректность
    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            String message = "Дата релиза: " + film.getReleaseDate() + " — не может быть раньше 28 декабря 1895 года";
            log.error(message);
            throw new ValidationException(message);
        }
        // Проверка рейтинга
        String query = "SELECT COALESCE(MAX(mpa_id), 0) FROM MPA";
        int maxRatingId = jdbc.queryForObject(query, Integer.class);
        if (film.getMpa().getId() > maxRatingId) {
            String message = "Райтинг: " + film.getMpa().getId() + " — Больше максимального: " + maxRatingId;
            log.error(message);
            throw new NotFoundException(message);
        }

        // Проверка жанра
        String queryGenre = "select max(genre_id) FROM genres";
        int maxGenreId = jdbc.queryForObject(queryGenre, Integer.class);
        int maxIdGenreInFilm = 0;
        for (Genre genre : film.getGenres()) {
            if (maxIdGenreInFilm < genre.getId()) {
                maxIdGenreInFilm = genre.getId();
            }
        }
        log.info("Метод: {}. Максимальный жанр фильма: {}", getMethod(), maxIdGenreInFilm);
        log.info("Метод: {}. Максимальный жанр Таблицы: {} ", getMethod(), maxGenreId);
        if (maxIdGenreInFilm > maxGenreId) {
            String message = "Жанр: " + maxIdGenreInFilm + " — Больше максимального: " + maxGenreId;
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    // Проверка существования фильма
    public boolean findFilm(long filmId) {
        log.info("Метод: {}. ID фильма: {}", getMethod(), filmId);
        String query = "SELECT count(film_id) FROM film WHERE film_id = ?";
        Integer count = jdbc.queryForObject(query, Integer.class, filmId);
        return count != null && count > 0;
    }


    // Возвращает имя метода для логирования
    private String getMethod() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }

    // Проверка пользователей
    private void checkUser(long id) {
        log.info("Метод: {}. ИД пользователя: {}", getMethod(), id);
        if (!userStorage.findUser(id)) {
            String message = "Пользователь с ID: " + id + " — не найден.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    // Проверка фильма
    private void checkFilm(long id) {
        log.info("Метод: {}. ID фильма: {}", getMethod(), id);
        if (!findFilm(id)) {
            String message = "Фильм с ID: " + id + " — не найден.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    // Очистка БД для тестов
    public void clearFilms() {
        String queryDeteteGenres = "DELETE FROM FILM_GENRES;";
        String queryDeteteLikes = "DELETE FROM LIKES;";
        String updateQuery = "DELETE FROM FILM;";
        try {
            int countGenres = jdbc.update(queryDeteteGenres);
            int countLikes = jdbc.update(queryDeteteLikes);
            int countUsers = jdbc.update(updateQuery);

            log.info("Метод: {}. Удалено Жанров: {}", getMethod(), countGenres);
            log.info("Метод: {}. Удалено Лайков: {}", getMethod(), countLikes);
            log.info("Метод: {}. Удалено фильмов: {}", getMethod(), countUsers);
        } catch (DataAccessException e) {
            e.getMessage();
        }
    }
}
