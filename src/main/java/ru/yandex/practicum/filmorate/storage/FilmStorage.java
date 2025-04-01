package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    // Создание фильма
    Film create(Film film);

    // Обновление фильма
    Film update(Film newFilm);

    // Получение всех фильмов
    Collection<Film> getAllFilms();

    // Получение фильма по ИД
    Film getFilm(long id);

    // Проверка существования фильма
    boolean findFilm(long id);
}
