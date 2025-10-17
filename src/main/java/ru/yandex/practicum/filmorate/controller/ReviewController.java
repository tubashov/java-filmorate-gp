package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // Создать новый отзыв.
    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        log.info("Получен запрос на создание отзыва: {}", review);
        Review created = reviewService.addReview(review);
        log.info("Отзыв создан: {}", created);
        return created;
    }

    // Обновить существующий отзыв.
    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info("Получен запрос на обновление отзыва: {}", review);
        Review updated = reviewService.updateReview(review);
        log.info("Отзыв обновлён: {}", updated);
        return updated;
    }

    // Удалить отзыв по ID.
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        log.info("Получен запрос на удаление отзыва с id={}", id);
        reviewService.deleteReview(id);
        log.info("Отзыв с id={} удалён", id);
    }

    // Получить отзыв по ID.
    @GetMapping("/{id}")
    public Review getReview(@PathVariable Long id) {
        log.info("Получен запрос на получение отзыва с id={}", id);
        return reviewService.getReviewById(id);
    }

    // Если указан filmId — вернуть отзывы только этого фильма. Отзывы сортируются по полезности.
    @GetMapping
    public List<Review> getAllReviews(@RequestParam(required = false) Long filmId,
                                      @RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос на получение отзывов: filmId={}, count={}", filmId, count);
        return reviewService.getReviews(filmId, count);
    }

    // Добавить лайк (оценка "полезно") отзыву.
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь {} отметил отзыв {} как полезный", userId, id);
        reviewService.addLike(id, userId);
    }

    // Добавить дизлайк (оценка "бесполезно") отзыву.
    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь {} отметил отзыв {} как бесполезный", userId, id);
        reviewService.addDislike(id, userId);
    }

    // Удалить лайк у отзыва.
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь {} удалил лайк у отзыва {}", userId, id);
        reviewService.removeLike(id, userId);
    }

    // Удалить дизлайк у отзыва.
    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь {} удалил дизлайк у отзыва {}", userId, id);
        reviewService.removeDislike(id, userId);
    }
}
