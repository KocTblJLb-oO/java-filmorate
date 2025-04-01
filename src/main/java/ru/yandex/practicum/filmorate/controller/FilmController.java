package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    /*
    ------------------------------------------------ Работа с фильмами
*/

    // Добавление фильма
    @PostMapping
    public Film creatFilm(@Valid @RequestBody Film film) {
        log.info("Метод: {}. Новый фильм: {}", getMethod(), film);
        return filmStorage.create(film);
    }

    // Обновление фильма
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        log.info("Метод: {}. Обновлённый фильм: {}", getMethod(), newFilm);
        return filmStorage.update(newFilm);
    }

    // Получение всех фильмов
    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    /*
    ------------------------------------------------ Работа с лайками
*/

    // Лайк фильму
    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @PathVariable("id") long id,
            @PathVariable("userId") long userId) {
        log.info("Метод: {}. ID фильма: {} ИД пользователя: {}", getMethod(), id, userId);

        filmService.addLike(id, userId);
    }

    // Удаление лайка
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") long id,
                           @PathVariable("userId") long userId) {
        log.info("Метод: {}. ID фильма: {} ИД пользователя: {}", getMethod(), id, userId);

        filmService.deleteLike(id, userId);
    }

    // Запрос топ фильмов
    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10") long count) {
        log.info("Метод: {}. Запрошено фильмов: {}", getMethod(), count);

        if (count <= 0) {
            throw new ValidationException("Количество фильмов не может быть меньше 1");
        }

        return filmService.getPopular(count);
    }

    /*
    ------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*/

    // Возвращает имя метода для логирования
    private String getMethod() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }
}
