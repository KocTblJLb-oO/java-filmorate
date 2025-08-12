package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.Collection;

@Slf4j
@Service
public class MPAService {
    private final MpaDbStorage mpaDbStorage;

    public MPAService(MpaDbStorage mpaBdStorage) {
        this.mpaDbStorage = mpaBdStorage;
    }

    /*
    ------------------------------------------------ Работа с MPA
*/
    // Имя MPA по ID
    @GetMapping("/{id}")
    public Mpa getMpaName(@PathVariable("id") long id) {
        log.info("Метод: {}. ИД МПА: {}", getMethod(), id);
        existsById(id);
        return mpaDbStorage.getMpaName(id);
    }

    @GetMapping
    public Collection<Mpa> getAllMpa() {
        return mpaDbStorage.getAllMpa();
    }

    // Проверка существования MPA
    public void existsById(long mpaId) {
        log.info("Метод: {}. ID MPA: {}", getMethod(), mpaId);
        if (!mpaDbStorage.existsById(mpaId)) {
            String message = "MPA с ID: " + mpaId + " — не найден.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    // Возвращает имя метода для логирования
    private String getMethod() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }
}
