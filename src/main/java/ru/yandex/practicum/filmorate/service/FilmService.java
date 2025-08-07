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


    public FilmService(@Qualifier("UserDbStorage") UserStorage userStorage, @Qualifier("FilmDbStorage") FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    // Лайк фильму
    public void addLike(long id, long userId) {
        log.info("Метод: {}. ID фильма: {} ИД пользователя: {}", getMethod(), id, userId);
        checkUser(userId);
        checkFilm(id);

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
        if (!userStorage.findUser(id)) {
            String message = "Пользователь с ID: " + id + " — не найден.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    // Проверка фильма
    public void checkFilm(long id) {
        if (!filmStorage.findFilm(id)) {
            String message = "Фильм с ID: " + id + " — не найден.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    // Возвращает имя метода для логирования
    private String getMethod() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }
}
