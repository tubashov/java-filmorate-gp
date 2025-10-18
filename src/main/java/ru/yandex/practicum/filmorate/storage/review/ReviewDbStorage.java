package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review review) {
        log.info("Создание нового отзыва пользователем {} для фильма {}",
                review.getUserId(), review.getFilmId());

        String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) " +
                "VALUES (?, ?, ?, ?, 0)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"review_id"});
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            return ps;
        }, keyHolder);

        review.setReviewId(keyHolder.getKey().longValue());
        review.setUseful(0);

        log.info("Отзыв успешно создан с id = {}", review.getReviewId());
        return review;
    }

    @Override
    public Review update(Review review) {
        log.info("Обновление отзыва с id = {}", review.getReviewId());

        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());

        Review updated = getById(review.getReviewId()).orElseThrow();
        log.info("Отзыв с id = {} успешно обновлён", review.getReviewId());
        return updated;
    }

    @Override
    public void delete(Long id) {
        log.info("Удаление отзыва с id = {}", id);
        jdbcTemplate.update("DELETE FROM reviews WHERE review_id = ?", id);
        log.info("Отзыв с id = {} успешно удалён", id);
    }

    @Override
    public Optional<Review> getById(Long id) {
        log.info("Получение отзыва по id = {}", id);

        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        List<Review> reviews = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Review r = new Review();
            r.setReviewId(rs.getLong("review_id"));
            r.setContent(rs.getString("content"));
            r.setIsPositive(rs.getBoolean("is_positive"));
            r.setUserId(rs.getLong("user_id"));
            r.setFilmId(rs.getLong("film_id"));
            r.setUseful(rs.getInt("useful"));
            return r;
        }, id);

        if (reviews.isEmpty()) {
            log.warn("Отзыв с id = {} не найден", id);
            return Optional.empty();
        }

        log.info("Отзыв с id = {} успешно найден", id);
        return Optional.of(reviews.get(0));
    }

    @Override
    public List<Review> getAll(Long filmId, int count) {
        log.info("Получение списка отзывов (filmId={}, count={})", filmId, count);

        String sql;
        Object[] params;

        if (filmId != null) {
            sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
            params = new Object[]{filmId, count};
        } else {
            sql = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
            params = new Object[]{count};
        }

        List<Review> reviews = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Review r = new Review();
            r.setReviewId(rs.getLong("review_id"));
            r.setContent(rs.getString("content"));
            r.setIsPositive(rs.getBoolean("is_positive"));
            r.setUserId(rs.getLong("user_id"));
            r.setFilmId(rs.getLong("film_id"));
            r.setUseful(rs.getInt("useful"));
            return r;
        }, params);

        log.info("Найдено {} отзывов", reviews.size());
        return reviews;
    }

    private void vote(Long reviewId, Long userId, boolean useful) {
        log.info("Пользователь {} {} отзыв {}", userId,
                useful ? "поставил лайк" : "поставил дизлайк", reviewId);

        String insertSql = "INSERT INTO review_likes (review_id, user_id, is_useful) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertSql, reviewId, userId, useful);

        int delta = useful ? 1 : -1;
        jdbcTemplate.update("UPDATE reviews SET useful = useful + ? WHERE review_id = ?", delta, reviewId);

        log.info("Оценка пользователя {} учтена. Полезность отзыва {} изменена на {}", userId, reviewId, delta);
    }

    private void removeVote(Long reviewId, Long userId, boolean useful) {
        log.info("Пользователь {} удалил {} у отзыва {}", userId,
                useful ? "лайк" : "дизлайк", reviewId);

        String deleteSql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_useful = ?";
        jdbcTemplate.update(deleteSql, reviewId, userId, useful);

        int delta = useful ? -1 : 1;
        jdbcTemplate.update("UPDATE reviews SET useful = useful + ? WHERE review_id = ?", delta, reviewId);

        log.info("Удалена оценка пользователя {}. Полезность отзыва {} изменена на {}", userId, reviewId, delta);
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        vote(reviewId, userId, true);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        vote(reviewId, userId, false);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        removeVote(reviewId, userId, true);
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        removeVote(reviewId, userId, false);
    }
}
