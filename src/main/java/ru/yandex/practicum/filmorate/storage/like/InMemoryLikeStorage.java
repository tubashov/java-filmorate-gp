package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.*;

@Component
public class InMemoryLikeStorage implements LikeStorage {
    private final Map<Long, Set<Long>> likes = new HashMap<>();

    @Override
    public void addLike(Long filmId, Long userId) {
        likes.computeIfAbsent(filmId, s -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        if (likes.containsKey(filmId)) {
            Set<Long> filmLikes = likes.get(filmId);
            if (!filmLikes.contains(userId)) {
                throw new NotFoundException("Лайк от пользователя " + userId + " для фильма " + filmId + " не найден");
            }
            filmLikes.remove(userId);
        } else {
            throw new NotFoundException("Лайки для фильма " + filmId + " не найдены");
        }
    }

    @Override
    public int getLikesCount(Long filmId) {
        return likes.getOrDefault(filmId, Collections.emptySet()).size();
    }
}