package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;


    public FilmService(@Qualifier("UserDbStorage") UserStorage userStorage,
                       @Qualifier("FilmDbStorage") FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Film create(Film film) {
        log.info("Метод: {}. Новый фильм: {}", getMethod(), film);
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        log.info("Метод: {}. Фильм для обновления: {}", getMethod(), newFilm);
        return filmStorage.update(newFilm);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilm(long id) {
        return filmStorage.getFilm(id);
    }

    // Лайк фильму
    public void addLike(long id, long userId) {
        filmStorage.addLike(id, userId);
    }

    // Удаление лайка
    public void deleteLike(long id, long userId) {
        checkUser(userId);
        checkFilm(id);
    }

    // Запрос топ фильмов
    public Collection<Film> getPopular(long count) {
        return filmStorage.getPopular(count);
    }

    /*
   ------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*/
    // Проверка пользователей
    private void checkUser(long id) {
        if (!userStorage.existsById(id)) {
            String message = "Пользователь с ID: " + id + " — не найден.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    // Проверка фильма
    public void checkFilm(long id) {
        if (!filmStorage.existsById(id)) {
            String message = "Фильм с ID: " + id + " — не найден.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    // Очистка БД для тестов
    public void clearFilms() {
        filmStorage.clearFilms();
    }

    // Возвращает имя метода для логирования
    private String getMethod() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }
}
