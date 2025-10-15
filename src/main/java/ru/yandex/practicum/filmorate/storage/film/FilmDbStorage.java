package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

    @Override
    public List<Film> getAll() {
        String sql = "SELECT f.*, m.name as mpa_name FROM films f LEFT JOIN mpa_ratings m ON f.mpa_id = m.id";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);
        loadGenresForFilms(films);
        loadLikesForFilms(films);
        return films;
    }

    @Override
    public Optional<Film> getById(Long id) {
        String sql = "SELECT f.*, m.name as mpa_name FROM films f LEFT JOIN mpa_ratings m ON f.mpa_id = m.id WHERE f.id = ?";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, id);
        if (films.isEmpty()) {
            return Optional.empty();
        }
        Film film = films.get(0);
        loadGenresForFilm(film);
        loadLikesForFilm(film);
        return Optional.of(film);
    }

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            if (film.getMpa() != null) {
                ps.setLong(5, film.getMpa().getId());
            } else {
                ps.setNull(5, java.sql.Types.BIGINT);
            }
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        updateFilmGenres(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId());

        updateFilmGenres(film);
        return getById(film.getId()).orElseThrow();
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private void loadGenresForFilms(List<Film> films) {
        if (films.isEmpty()) return;

        String filmIds = films.stream()
                .map(Film::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String sql = String.format(
                "SELECT fg.film_id, g.id, g.name FROM film_genres fg JOIN genres g ON fg.genre_id = g.id WHERE fg.film_id IN (%s)",
                filmIds
        );

        Map<Long, Set<Genre>> filmGenresMap = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            Long filmId = rs.getLong("film_id");
            Genre genre = new Genre(rs.getLong("id"), rs.getString("name"));
            filmGenresMap.computeIfAbsent(filmId, k -> new HashSet<>()).add(genre);
        });

        for (Film film : films) {
            film.setGenres(filmGenresMap.getOrDefault(film.getId(), new HashSet<>()));
        }
    }

    private void loadGenresForFilm(Film film) {
        String sql = "SELECT g.id, g.name FROM film_genres fg JOIN genres g ON fg.genre_id = g.id WHERE fg.film_id = ?";
        Set<Genre> genres = new HashSet<>(jdbcTemplate.query(sql,
                new Object[]{film.getId()},
                (rs, rowNum) -> new Genre(rs.getLong("id"), rs.getString("name"))));
        film.setGenres(genres);
    }

    private void loadLikesForFilms(List<Film> films) {
        if (films.isEmpty()) return;

        String filmIds = films.stream()
                .map(Film::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String sql = String.format(
                "SELECT film_id, user_id FROM film_likes WHERE film_id IN (%s)",
                filmIds
        );

        Map<Long, Set<Long>> filmLikesMap = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            Long filmId = rs.getLong("film_id");
            Long userId = rs.getLong("user_id");
            filmLikesMap.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
        });

        for (Film film : films) {
            film.setLikes(filmLikesMap.getOrDefault(film.getId(), new HashSet<>()));
        }
    }

    private void loadLikesForFilm(Film film) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        Set<Long> likes = new HashSet<>(jdbcTemplate.query(sql,
                new Object[]{film.getId()},
                (rs, rowNum) -> rs.getLong("user_id")));
        film.setLikes(likes);
    }

    private void updateFilmGenres(Film film) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            List<Object[]> batchArgs = film.getGenres().stream()
                    .map(genre -> new Object[]{film.getId(), genre.getId()})
                    .collect(Collectors.toList());
            jdbcTemplate.batchUpdate(sql, batchArgs);
        }
    }
}