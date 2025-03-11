package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long currentMaxId = 0L;

    @Override
    // Создание фильма
    public Film create(Film film) {
        log.info("Метод: {}. Новый фильм: {}", getMethod(), film);

        validate(film);

        film.setId(getNextId());
        films.put(film.getId(), film);

        return film;
    }

    // Обновление фильма
    @Override
    public Film update(Film newFilm) {
        if (!films.containsKey(newFilm.getId())) {
            String message = "Фильм с ID: " + newFilm.getId() + " — не найден";
            log.error(message);
            throw new NotFoundException(message);
        }

        validate(newFilm);
        films.put(newFilm.getId(), newFilm);

        log.info("Метод: {}. Обновлённый фильм: {}", getMethod(), newFilm);
        return newFilm;
    }

    // Получение всех фильмов
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    // Получение фильма по ИД
    @Override
    public Film getFilm(long id) {
        return films.get(id);
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
        // Если у фильма нет списка для лайков, добавляем его
        if (film.getLikes() == null) {
            log.trace("Метод: {}. У фильма нет лайков.", getMethod());
            film.setLikes(new HashSet<>());
        }
    }

    // Проверка существования фильма
    public boolean findFilm(long id) {
        return films.containsKey(id);
    }

    // Возвращает имя метода для логирования
    private String getMethod() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }
}
