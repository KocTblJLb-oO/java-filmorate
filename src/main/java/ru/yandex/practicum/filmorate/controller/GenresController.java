package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenresService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenresController {
    private final GenresService genresService;

    public GenresController(GenresService genresService) {
        this.genresService = genresService;
    }

    /*
    ------------------------------------------------ Работа с Жанрами
*/
    // Имя Жанра по ID
    @GetMapping("/{id}")
    public Genre getMpaName(@PathVariable("id") long id) {
        log.info("Метод: {}. ИД МПА: {}", getMethod(), id);
        return genresService.getGenreName(id);
    }

    @GetMapping
    public Collection<Genre> getAllGenre() {
        return genresService.getAllGenre();
    }

            /*
    ------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*/

    // Возвращает имя метода для логирования
    private String getMethod() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }
}
