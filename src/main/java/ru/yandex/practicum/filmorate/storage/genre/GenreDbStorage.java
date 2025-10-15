package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAll() {
        String sql = "SELECT * FROM genres ORDER BY id";
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    @Override
    public Optional<Genre> getById(Long id) {
        String sql = "SELECT * FROM genres WHERE id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, this::mapRowToGenre, id);
        return genres.isEmpty() ? Optional.empty() : Optional.of(genres.get(0));
    }

    private Genre mapRowToGenre(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new Genre(
                rs.getLong("id"),
                rs.getString("name")
        );
    }

    @Override
    public Set<Long> getExistingGenreIds(Set<Long> genreIds) {
        if (genreIds.isEmpty()) {
            return Collections.emptySet();
        }

        String inClause = genreIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String sql = "SELECT id FROM genres WHERE id IN (" + inClause + ")";

        return new HashSet<>(jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getLong("id")));
    }
}