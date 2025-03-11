package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public FilmService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    // Лайк фильму
    public void addLike(long id, long userId) {
        checkUser(userId);
        checkFilm(id);

        filmStorage.getFilm(id).getLikes().add(userId);
    }

    // Удаление лайка
    public void deleteLike(long id, long userId) {
        checkUser(userId);
        checkFilm(id);

        filmStorage.getFilm(id).getLikes().remove(userId);
    }

    // Запрос топ фильмов
    public Collection<Film> getPopular(long count) {
        Comparator<Film> comparator = Comparator.comparing((Film film) -> film.getLikes() != null ?
                        film.getLikes().size() : 0)
                .reversed();

        return filmStorage.getAllFilms()
                .stream()
                .sorted(comparator)
                .limit(count)
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

    // Проверка фильма
    private void checkFilm(long id) {
        if (!filmStorage.findFilm(id)) {
            String message = "Фильм с ID: " + id + " — не найден.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }
}
