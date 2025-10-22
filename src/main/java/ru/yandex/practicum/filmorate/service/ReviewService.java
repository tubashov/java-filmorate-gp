package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserService userService;
    private final FilmService filmService;
    private final FeedService feedService;

    public Review addReview(Review review) {
        validateUser(review.getUserId());
        validateFilm(review.getFilmId());
        review.setUseful(0);
        Review created = reviewStorage.create(review);

        feedService.addEvent(new Event(
                null,
                created.getUserId(),
                created.getReviewId(),
                Event.EventType.REVIEW,
                Event.Operation.ADD,
                System.currentTimeMillis()
        ));

        log.info("Review added: {}", created);
        return created;
    }

    public Review updateReview(Review review) {
        Review existing = getReviewById(review.getReviewId());
        validateUser(review.getUserId());
        validateFilm(review.getFilmId());

        existing.setContent(review.getContent());
        existing.setIsPositive(review.getIsPositive());
        Review updated = reviewStorage.update(existing);

        feedService.addEvent(new Event(
                null,
                updated.getUserId(),
                updated.getReviewId(),
                Event.EventType.REVIEW,
                Event.Operation.UPDATE,
                System.currentTimeMillis()
        ));

        log.info("Review updated: {}", updated);
        return updated;
    }

    public void deleteReview(Long reviewId) {
        Review review = getReviewById(reviewId);
        reviewStorage.delete(reviewId);

        feedService.addEvent(new Event(
                null,
                review.getUserId(),
                review.getReviewId(),
                Event.EventType.REVIEW,
                Event.Operation.REMOVE,
                System.currentTimeMillis()
        ));

        log.info("Review deleted: {}", reviewId);
    }

    public Review getReviewById(Long reviewId) {
        return reviewStorage.getById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review with ID " + reviewId + " not found"));
    }

    public List<Review> getReviews(Long filmId, int count) {
        if (filmId != null) validateFilm(filmId);
        return reviewStorage.getAll(filmId, count);
    }

    public void addLike(Long reviewId, Long userId) {
        validateUser(userId);
        getReviewById(reviewId);
        reviewStorage.addLike(reviewId, userId);

        feedService.addEvent(new Event(
                null,
                userId,
                reviewId,
                Event.EventType.LIKE,
                Event.Operation.ADD,
                System.currentTimeMillis()
        ));

        log.info("Пользователь {} поставил лайк отзыву {}", userId, reviewId);
    }

    public void removeLike(Long reviewId, Long userId) {
        validateUser(userId);
        getReviewById(reviewId);
        reviewStorage.removeLike(reviewId, userId);

        feedService.addEvent(new Event(
                null,
                userId,
                reviewId,
                Event.EventType.LIKE,
                Event.Operation.REMOVE,
                System.currentTimeMillis()
        ));

        log.info("Пользователь {} удалил лайк отзыву {}", userId, reviewId);
    }

    public void addDislike(Long reviewId, Long userId) {
        validateUser(userId);
        getReviewById(reviewId);
        reviewStorage.addDislike(reviewId, userId);
        log.info("Пользователь {} поставил дизлайк отзыву {}", userId, reviewId);
    }

    public void removeDislike(Long reviewId, Long userId) {
        validateUser(userId);
        getReviewById(reviewId);
        reviewStorage.removeDislike(reviewId, userId);
        log.info("Пользователь {} удалил дизлайк отзыву {}", userId, reviewId);
    }

    private void validateUser(Long userId) {
        userService.getUserById(userId);
    }

    private void validateFilm(Long filmId) {
        try {
            filmService.getFilmById(filmId);
        } catch (NotFoundException e) {
            throw new NotFoundException("Film not found");
        }
    }
}
