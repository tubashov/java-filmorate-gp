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

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        log.info("Создание отзыва: {}", review);
        Review created = reviewService.addReview(review);
        log.info("Отзыв создан: {}", created);
        return created;
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info("Обновление отзыва: {}", review);
        Review updated = reviewService.updateReview(review);
        log.info("Отзыв обновлён: {}", updated);
        return updated;
    }

    @DeleteMapping("/{reviewId}")
    public void deleteReview(@PathVariable Long reviewId) {
        log.info("Удаление отзыва с reviewId {}", reviewId);
        reviewService.deleteReview(reviewId);
        log.info("Отзыв с reviewId {} удалён", reviewId);
    }

    @GetMapping("/{reviewId}")
    public Review getReview(@PathVariable Long reviewId) {
        log.info("Получение отзыва с reviewId {}", reviewId);
        return reviewService.getReviewById(reviewId);
    }

    @GetMapping
    public List<Review> getAllReviews(@RequestParam(required = false) Long filmId,
                                      @RequestParam(defaultValue = "10") int count) {
        log.info("Получение отзывов: filmId={}, count={}", filmId, count);
        return reviewService.getReviews(filmId, count);
    }

    // Лайк и дизлайк теперь работают по URL-параметрам, тело запроса не нужно
    @PutMapping("/{reviewId}/like/{userId}")
    public void addLike(@PathVariable Long reviewId,
                        @PathVariable Long userId,
                        @RequestBody(required = false) Review review) {
        log.info("Пользователь {} поставил лайк отзыву {}", userId, reviewId);
        reviewService.addLike(reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public void addDislike(@PathVariable Long reviewId,
                           @PathVariable Long userId,
                           @RequestBody(required = false) Review review) {
        log.info("Пользователь {} поставил дизлайк отзыву {}", userId, reviewId);
        reviewService.addDislike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public void removeLike(@PathVariable Long reviewId, @PathVariable Long userId) {
        log.info("Пользователь {} удалил лайк у отзыва {}", userId, reviewId);
        reviewService.removeLike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public void removeDislike(@PathVariable Long reviewId, @PathVariable Long userId) {
        log.info("Пользователь {} удалил дизлайк у отзыва {}", userId, reviewId);
        reviewService.removeDislike(reviewId, userId);
    }
}
