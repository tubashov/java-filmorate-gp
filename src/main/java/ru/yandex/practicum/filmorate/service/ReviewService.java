package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public ReviewService(@Qualifier("reviewDbStorage") ReviewStorage reviewStorage,
                         UserStorage userStorage,
                         FilmStorage filmStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Review addReview(Review review) {
        validateReview(review);
        ensureUserExists(review.getUserId());
        ensureFilmExists(review.getFilmId());
        review.setUseful(0);
        return reviewStorage.create(review);
    }

    public Review updateReview(Review review) {
        ensureReviewExists(review.getReviewId());
        validateReview(review);
        return reviewStorage.update(review);
    }

    public void deleteReview(Long reviewId) {
        ensureReviewExists(reviewId);
        reviewStorage.delete(reviewId);
    }

    public Review getReviewById(Long reviewId) {
        return reviewStorage.getById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("Отзыв не найден"));
    }

    public List<Review> getReviews(Long filmId, int count) {
        return reviewStorage.getAll(filmId, count);
    }

    public void addLike(Long reviewId, Long userId) {
        Review review = ensureReviewExists(reviewId);
        ensureUserExists(userId);
        reviewStorage.addLike(reviewId, userId);
    }

    public void addDislike(Long reviewId, Long userId) {
        Review review = ensureReviewExists(reviewId);
        ensureUserExists(userId);
        reviewStorage.addDislike(reviewId, userId);
    }

    public void removeLike(Long reviewId, Long userId) {
        Review review = ensureReviewExists(reviewId);
        ensureUserExists(userId);
        reviewStorage.removeLike(reviewId, userId);
    }

    public void removeDislike(Long reviewId, Long userId) {
        Review review = ensureReviewExists(reviewId);
        ensureUserExists(userId);
        reviewStorage.removeDislike(reviewId, userId);
    }

    private Review ensureReviewExists(Long reviewId) {
        return reviewStorage.getById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("Отзыв не найден"));
    }

    private void ensureUserExists(Long userId) {
        if (userStorage.getById(userId).isEmpty()) {
            throw new NoSuchElementException("Пользователь не найден");
        }
    }

    private void ensureFilmExists(Long filmId) {
        if (filmStorage.getById(filmId).isEmpty()) {
            throw new NoSuchElementException("Фильм не найден");
        }
    }

    private void validateReview(Review review) {
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new IllegalArgumentException("Содержание отзыва не может быть пустым");
        }
        if (review.getUserId() == null || review.getFilmId() == null) {
            throw new IllegalArgumentException("Отзыв должен принадлежать пользователю и фильму");
        }
    }
}
