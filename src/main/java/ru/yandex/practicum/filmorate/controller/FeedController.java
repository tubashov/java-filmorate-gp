package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.service.FeedService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{id}/feed")
@Slf4j
@Validated
public class FeedController {

    private final FeedService feedService;

    @PostMapping
    public void addEvent(@PathVariable("id") @Positive Long userId, @RequestBody Event event) {
        log.info("Запрос на добавление события для пользователя с ID: {}", userId);

        if (!userId.equals(event.getUserId())) {
            log.warn("Несоответствие ID пользователя в URL и в событии: {} vs {}", userId, event.getUserId());
            throw new IllegalArgumentException("ID пользователя в URL и в событии не совпадают");
        }

        feedService.addEvent(event);
        log.info("Событие {} успешно добавлено для пользователя {}", event.getEventId(), userId);
    }

    @GetMapping
    public List<Event> getUserFeed(@PathVariable("id") @Positive Long userId) {
        log.info("Получение ленты событий для пользователя с ID: {}", userId);
        List<Event> feed = feedService.getUserFeed(userId);
        log.info("Лента событий для пользователя {} успешно получена ({} событий)", userId, feed.size());
        return feed;
    }
}
