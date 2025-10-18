package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;

    @Autowired
    public ReviewService(@Qualifier("reviewDbStorage") ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    // Создание нового отзыва
    public Review addReview(Review review) {
        validateReview(review);
        review.setUseful(0); // Изначально полезность = 0
        return reviewStorage.create(review);
    }

    // Обновление существующего отзыва
    public Review updateReview(Review review) {
        ensureReviewExists(review.getReviewId());
        validateReview(review);
        return reviewStorage.update(review);
    }

    // Удаление отзыва
    public void deleteReview(Long reviewId) {
        ensureReviewExists(reviewId);
        reviewStorage.delete(reviewId);
    }

    // Получение одного отзыва
    public Review getReviewById(Long reviewId) {
        return reviewStorage.getById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("Отзыв не найден"));
    }

    // Получение списка отзывов по фильму или всех
    public List<Review> getReviews(Long filmId, int count) {
        return reviewStorage.getAll(filmId, count);
    }

    // Добавить лайк
    public void addLike(Long reviewId, Long userId) {
        Review review = ensureReviewExists(reviewId);
        reviewStorage.addLike(reviewId, userId);
        review.setUseful(review.getUseful() + 1);
        reviewStorage.update(review);
    }

    // Добавить дизлайк
    public void addDislike(Long reviewId, Long userId) {
        Review review = ensureReviewExists(reviewId);
        reviewStorage.addDislike(reviewId, userId);
        review.setUseful(review.getUseful() - 1);
        reviewStorage.update(review);
    }

    // Удалить лайк
    public void removeLike(Long reviewId, Long userId) {
        Review review = ensureReviewExists(reviewId);
        reviewStorage.removeLike(reviewId, userId);
        review.setUseful(review.getUseful() - 1);
        reviewStorage.update(review);
    }

    // Удалить дизлайк
    public void removeDislike(Long reviewId, Long userId) {
        Review review = ensureReviewExists(reviewId);
        reviewStorage.removeDislike(reviewId, userId);
        review.setUseful(review.getUseful() + 1);
        reviewStorage.update(review);
    }

    // Проверка существования отзыва и возврат объекта
    private Review ensureReviewExists(Long reviewId) {
        return reviewStorage.getById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("Отзыв не найден"));
    }

    // Валидация отзыва перед созданием/обновлением
    private void validateReview(Review review) {
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new IllegalArgumentException("Содержание отзыва не может быть пустым");
        }
        if (review.getUserId() == null || review.getFilmId() == null) {
            throw new IllegalArgumentException("Отзыв должен принадлежать пользователю и фильму");
        }
    }
}
