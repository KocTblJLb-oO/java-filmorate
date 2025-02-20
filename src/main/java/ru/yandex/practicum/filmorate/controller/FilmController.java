package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private long currentMaxId = 0L;

    // Добавление фильма
    @PostMapping
    public Film creatFilm(@Valid @RequestBody Film film) {
        log.info("Метод: {}. Новый фильм: {}", getMethod(), film);

        validate(film);

        film.setId(getNextId());
        films.put(film.getId(), film);

        return film;
    }

    // Обновление фильма
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        if (!films.containsKey(newFilm.getId())) {
            String message = "Фильм с ID: " + newFilm.getId() + " — не найден";
            log.error(message);
            throw new ValidationException(message);
        }

        validate(newFilm);
        films.put(newFilm.getId(), newFilm);

        log.info("Метод: {}. Обновлённый фильм: {}", getMethod(), newFilm);
        return newFilm;
    }

    // Получение всех фильмов
    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
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
    }

    // Возвращает имя метода для логирования
    private String getMethod() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }
}
