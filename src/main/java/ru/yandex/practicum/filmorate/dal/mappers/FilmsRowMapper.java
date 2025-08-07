package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class FilmsRowMapper implements RowMapper<Film> {

    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;

    public FilmsRowMapper(GenreDbStorage genreDbStorage, MpaDbStorage mpaDbStorage) {
        this.genreDbStorage = genreDbStorage;
        this.mpaDbStorage = mpaDbStorage;
    }

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("film_id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));

        Set<Genre> genres = new LinkedHashSet<>(genreDbStorage.getGenresByFilmId(film.getId()));
        film.setGenres(genres);

        int mpaId = resultSet.getInt("mpa_id");
        film.setMpa(mpaDbStorage.getMpaName(mpaId));

        return film;
    }
}