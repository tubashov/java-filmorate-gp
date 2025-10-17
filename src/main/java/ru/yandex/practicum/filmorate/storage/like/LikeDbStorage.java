package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public int getLikesCount(Long filmId) {
        String sql = "SELECT COUNT(*) FROM film_likes WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, filmId);
    }

    @Override
    public Set<Long> getCommonLikedFilms(Long userId, Long friendId) {
        String sql = "SELECT f1.film_id " +
                "FROM film_likes f1 " +
                "JOIN film_likes f2 ON f1.film_id = f2.film_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";

        return new HashSet<>(jdbcTemplate.query(sql,
                new Object[]{userId, friendId},
                (rs, rowNum) -> rs.getLong("film_id")));
    }
}