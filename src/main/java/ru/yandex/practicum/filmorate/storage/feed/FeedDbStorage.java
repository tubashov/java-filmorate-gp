package ru.yandex.practicum.filmorate.storage.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.EventRowMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.PreparedStatement;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Event addEvent(Event event) {
        // Валидация обязательных полей
        if (event.getUserId() == null ||
                event.getEventType() == null ||
                event.getOperation() == null ||
                event.getEntityId() == null ||
                event.getTimestamp() == null) {
            throw new ValidationException("Все обязательные поля события " +
                                          "(userId, eventType, operation, entityId, timestamp) должны быть заполнены");
        }

        String sql = "INSERT INTO feed_events (timestamp, user_id, event_type, operation, entity_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"event_id"});
            ps.setLong(1, event.getTimestamp());
            ps.setLong(2, event.getUserId());
            ps.setString(3, event.getEventType().name());
            ps.setString(4, event.getOperation().name());
            ps.setLong(5, event.getEntityId());
            return ps;
        }, keyHolder);

        // Получаем сгенерированный event_id и записываем обратно в объект
        Number key = keyHolder.getKey();
        if (key != null) {
            event.setEventId(key.longValue());
        }

        return event;
    }


    @Override
    public List<Event> getUserFeed(Long userId) {
        String sql = "SELECT * FROM feed_events WHERE user_id = ? ORDER BY timestamp";
        return jdbcTemplate.query(sql, new EventRowMapper(), userId);
    }
}
