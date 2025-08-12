package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

@Slf4j
@Component
public class GenreDbStorage {

    private final JdbcTemplate jdbc;
    private final GenreRowMapper mapper;

    public GenreDbStorage(JdbcTemplate jdbcTemplate, GenreRowMapper mapper) {
        this.jdbc = jdbcTemplate;
        this.mapper = mapper;
    }

    public List<Genre> getGenresByFilmId(long filmId) {
        String sql = "SELECT g.genre_id, g.name " +
                "FROM GENRES g " +
                "JOIN FILM_GENRES fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY g.genre_id";
        return jdbc.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId);
    }

    public Genre getGenreName(long id) {
        existsById(id);
        String query =
                "SELECT * FROM Genres " +
                        "where genre_id = ?";
        return jdbc.queryForObject(query, mapper, id);
    }

    public Collection<Genre> getAllGenre() {
        String query = "SELECT * FROM Genres";
        return jdbc.query(query, mapper);
    }

    /*
    ------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*/

    // Проверка существования Жанра
    public boolean existsById(long genreId) {
        log.info("Метод: {}. ID Жанра: {}", getMethod(), genreId);
        String query = "SELECT count(*) FROM Genres WHERE genre_id = ?";
        Integer count = jdbc.queryForObject(query, Integer.class, genreId);
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