package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {

    private final FeedStorage feedStorage;
    private final UserStorage userStorage;

    public List<Event> getUserFeed(Long userId) {
        if (userStorage.getById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        log.info("Запрос ленты событий для пользователя с ID: {}", userId);
        List<Event> events = feedStorage.getUserFeed(userId);
        log.info("Получена лента из {} событий для пользователя {}", events.size(), userId);
        return events;
    }

    public void addEvent(Event event) {
        if (userStorage.getById(event.getUserId()).isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + event.getUserId() + " не найден");
        }

        if (event.getEventType() == null || event.getOperation() == null || event.getEntityId() == null) {
            log.warn("Попытка добавить некорректное событие: {}", event);
            throw new ValidationException("Событие содержит некорректные данные");
        }

        if (event.getEventType() == Event.EventType.LIKE && event.getOperation() == Event.Operation.ADD) {
            boolean alreadyLiked = feedStorage.hasUserLikedReview(event.getUserId(), event.getEntityId());
            if (alreadyLiked) {
                log.info("Пользователь {} уже поставил лайк объекту {}. Событие в feed не добавлено.",
                        event.getUserId(), event.getEntityId());
                return;
            }
        }

        log.info("Добавление нового события: {} (тип={}, операция={}, сущность={})",
                event.getEventId(), event.getEventType(), event.getOperation(), event.getEntityId());

        feedStorage.addEvent(event);

        log.info("Событие {} успешно добавлено в ленту пользователя {}", event.getEventId(), event.getUserId());
    }
}
