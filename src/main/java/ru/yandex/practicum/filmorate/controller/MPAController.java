package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MPAService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MPAController {
    private final MPAService mpaService;

    public MPAController(MPAService mpaService) {
        this.mpaService = mpaService;
    }

    /*
    ------------------------------------------------ Работа с MPA
*/
    // Имя MPA по ID
    @GetMapping("/{id}")
    public Mpa getMpaName(@PathVariable("id") long id) {
        log.info("Метод: {}. ИД МПА: {}", getMethod(), id);
        return mpaService.getMpaName(id);
    }

    @GetMapping
    public Collection<Mpa> getAllMpa() {
        return mpaService.getAllMpa();
    }

    // Возвращает имя метода для логирования
    private String getMethod() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }
}
