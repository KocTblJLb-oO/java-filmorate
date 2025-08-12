package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.Collection;

@Slf4j
@Service
public class GenresService {
    private final GenreDbStorage genreDbStorage;


    public GenresService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    // Имя Жанра по ID
    public Genre getGenreName(long id) {
        log.info("Метод: {}. ИД МПА: {}", getMethod(), id);
        existsById(id);
        return genreDbStorage.getGenreName(id);
    }

    public Collection<Genre> getAllGenre() {
        return genreDbStorage.getAllGenre();
    }

    /*
    ------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*/

    // Проверка существования Жанра
    public void existsById(long genreId) {
        if (!genreDbStorage.existsById(genreId)) {
            String message = "Жанр с ID: " + genreId + " — не найден.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    // Возвращает имя метода для логирования
    private String getMethod() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }
}
