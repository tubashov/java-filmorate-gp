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
    @DeleteMapping("/{reviewId}")
    public void deleteReview(@PathVariable Long reviewId) {
        log.info("Получен запрос на удаление отзыва с reviewId={}", reviewId);
        reviewService.deleteReview(reviewId);
        log.info("Отзыв с reviewId={} удалён", reviewId);
    }

    // Получить отзыв по ID.
    @GetMapping("/{reviewId}")
    public Review getReview(@PathVariable Long reviewId) {
        log.info("Получен запрос на получение отзыва с reviewId={}", reviewId);
        return reviewService.getReviewById(reviewId);
    }

    // Если указан filmId — вернуть отзывы только этого фильма. Отзывы сортируются по полезности.
    @GetMapping
    public List<Review> getAllReviews(@RequestParam(required = false) Long filmId,
                                      @RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос на получение отзывов: filmId={}, count={}", filmId, count);
        return reviewService.getReviews(filmId, count);
    }

    // Добавить лайк (оценка "полезно") отзыву.
    @PutMapping("/{reviewId}/like/{userId}")
    public void addLike(@PathVariable Long reviewId, @PathVariable Long userId) {
        log.info("Пользователь {} отметил отзыв {} как полезный", userId, reviewId);
        reviewService.addLike(reviewId, userId);
    }

    // Добавить дизлайк (оценка "бесполезно") отзыву.
    @PutMapping("/{reviewId}/dislike/{userId}")
    public void addDislike(@PathVariable Long reviewId, @PathVariable Long userId) {
        log.info("Пользователь {} отметил отзыв {} как бесполезный", userId, reviewId);
        reviewService.addDislike(reviewId, userId);
    }

    // Удалить лайк у отзыва.
    @DeleteMapping("/{reviewId}/like/{userId}")
    public void removeLike(@PathVariable Long reviewId, @PathVariable Long userId) {
        log.info("Пользователь {} удалил лайк у отзыва {}", userId, reviewId);
        reviewService.removeLike(reviewId, userId);
    }

    // Удалить дизлайк у отзыва.
    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public void removeDislike(@PathVariable Long reviewId, @PathVariable Long userId) {
        log.info("Пользователь {} удалил дизлайк у отзыва {}", userId, reviewId);
        reviewService.removeDislike(reviewId, userId);
    }
}
