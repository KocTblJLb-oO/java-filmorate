package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Component
@Slf4j
public class MpaDbStorage {
    private final JdbcTemplate jdbc;
    private final MpaRowMapper mapper;

    public MpaDbStorage(JdbcTemplate jdbc, MpaRowMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    public Mpa getMpaName(long id) {
        String query =
                "SELECT * FROM Mpa " +
                        "where mpa_id = ?";
        return jdbc.queryForObject(query, mapper, id);
    }

    public Collection<Mpa> getAllMpa() {
        String query = "SELECT * FROM Mpa";
        return jdbc.query(query, mapper);
    }

        /*
    ------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*/

    // Проверка существования MPA
    public boolean existsById(long mpaId) {
        log.info("Метод: {}. ID MPA: {}", getMethod(), mpaId);
        String query = "SELECT count(*) FROM Mpa WHERE mpa_id = ?";
        Integer count = jdbc.queryForObject(query, Integer.class, mpaId);
        if (count == null || count == 0) {
            return false;
        }
        return true;
    }

    // Возвращает имя метода для логирования
    private String getMethod() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }
}
