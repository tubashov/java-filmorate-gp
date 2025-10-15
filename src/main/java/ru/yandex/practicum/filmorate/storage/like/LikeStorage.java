package ru.yandex.practicum.filmorate.storage.like;

public interface LikeStorage {
    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    int getLikesCount(Long filmId);
}
