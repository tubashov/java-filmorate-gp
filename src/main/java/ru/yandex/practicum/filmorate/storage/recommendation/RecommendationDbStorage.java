package ru.yandex.practicum.filmorate.storage.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RecommendationDbStorage implements RecommendationStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Long> getRecommendation(Long userId) {
        String sql = "SELECT film_id FROM film_likes " +
                "WHERE user_id = ? AND film_id NOT IN (SELECT film_id FROM film_likes WHERE user_id = ?)";
        Long userWithSameTasteId = getUserWithSameTasteId(userId);
        List<Long> films = new ArrayList<>();
        films.addAll(jdbcTemplate.queryForList(sql, Long.class, userWithSameTasteId, userId));
        return films;
    }

    private Long getUserWithSameTasteId(Long userId) {
        String sql = "SELECT t2.user_id " +
                "FROM film_likes AS t1 " +
                "LEFT JOIN film_likes AS t2 ON t1.film_id = t2.film_id " +
                "WHERE t1.user_id = ? and t2.user_id <> t1.user_id " +
                "GROUP BY t2.user_id " +
                "ORDER BY COUNT(*) DESC LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(sql, Long.class, userId);
        } catch (RuntimeException e) {
            return null;
        }
    }
}
