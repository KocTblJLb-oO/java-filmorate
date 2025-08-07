package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenresController {
    private GenreDbStorage genreDbStorage;

    public GenresController(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    /*
    ------------------------------------------------ Работа с Жанрами
*/
    // Имя Жанра по ID
    @GetMapping("/{id}")
    public Genre getMpaName(@PathVariable("id") long id) {
        log.info("Метод: {}. ИД МПА: {}", getMethod(), id);
        return genreDbStorage.getMpaName(id);
    }

    @GetMapping
    public Collection<Genre> getAllGenre() {
        return genreDbStorage.getAllGenre();
    }

            /*
    ------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*/

    // Возвращает имя метода для логирования
    private String getMethod() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }
}
