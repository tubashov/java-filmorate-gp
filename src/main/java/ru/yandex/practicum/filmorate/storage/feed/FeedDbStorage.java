package ru.yandex.practicum.filmorate.storage.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mappers.EventRowMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addEvent(Event event) {
        String sql = "INSERT INTO feed_events (timestamp, user_id, event_type, operation, entity_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                event.getTimestamp(),
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId());
    }

    @Override
    public List<Event> getUserFeed(Long userId) {
        String sql = "SELECT * FROM feed_events WHERE user_id = ? ORDER BY timestamp";
        return jdbcTemplate.query(sql, new EventRowMapper(), userId);
    }

    @Override
    public boolean hasUserLikedReview(Long userId, Long reviewId) {
        String sql = "SELECT COUNT(*) FROM review_likes WHERE user_id = ? AND review_id = ? AND is_useful = true";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, reviewId);
        return count != null && count > 0;
    }
}
