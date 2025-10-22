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
    private final UserStorage userStorage; // <-- используем UserStorage, а не UserService

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

        if (event.getEventType() == Event.EventType.LIKE) {
            switch (event.getOperation()) {
                case ADD -> {
                    if (feedStorage.hasUserLikedReview(event.getUserId(), event.getEntityId())) {
                        log.warn("Пользователь {} уже поставил лайк объекту {}", event.getUserId(), event.getEntityId());
                        throw new ValidationException("Пользователь уже поставил лайк этому отзыву");
                    }
                }
                case UPDATE, REMOVE -> {
                }
            }
        }

        log.info("Добавление нового события: {} (тип={}, операция={}, сущность={})",
                event.getEventId(), event.getEventType(), event.getOperation(), event.getEntityId());

        feedStorage.addEvent(event);

        log.info("Событие {} успешно добавлено в ленту пользователя {}", event.getEventId(), event.getUserId());
    }
}
