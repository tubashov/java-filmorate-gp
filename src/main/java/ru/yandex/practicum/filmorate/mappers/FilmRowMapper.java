package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));

        LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
        film.setReleaseDate(releaseDate);

        film.setDuration(resultSet.getInt("duration"));

        if (resultSet.getLong("mpa_id") != 0) {
            Mpa mpa = new Mpa();
            mpa.setId(resultSet.getLong("mpa_id"));
            mpa.setName(resultSet.getString("mpa_name"));
            film.setMpa(mpa);
        }

        return film;
    }
}