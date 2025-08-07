package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;
    private final FilmDbStorage filmDbStorage;

    public FilmController(@Qualifier("FilmDbStorage") FilmStorage filmStorage, FilmService filmService, GenreDbStorage genreDbStorage, FilmDbStorage filmDbStorage) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
        this.filmDbStorage = filmDbStorage;
    }

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
    ------------------------------------------------ Фильмы с жанрами
*/

    @GetMapping("/{id}")
    public Film getFilmsWithGenre(@PathVariable("id") long idFilm) {
        filmService.checkFilm(idFilm);
        log.info("Метод: {}. Жанр: {}", getMethod(), idFilm);

        return filmDbStorage.getFilm(idFilm);
    }

    /*
    ------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*/

    // Возвращает имя метода для логирования
    private String getMethod() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }

    // Удаление всех фильмов из БД для тестирования приложения
    // http://localhost:8080/films/delete
    @GetMapping("/delete")
    public void clearFilms() {
        log.info("Метод: {}.", getMethod());
        filmStorage.clearFilms();
    }
}
