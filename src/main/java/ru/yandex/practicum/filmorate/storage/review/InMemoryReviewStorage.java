package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.*;

@Component
public class InMemoryReviewStorage implements ReviewStorage {

    private final Map<Long, Review> reviews = new HashMap<>();
    private final Map<Long, Map<Long, Boolean>> reviewVotes = new HashMap<>();
    private long nextId = 1;

    @Override
    public Review create(Review review) {
        review.setReviewId(nextId++);
        review.setUseful(0);
        reviews.put(review.getReviewId(), review);
        return review;
    }

    @Override
    public Review update(Review review) {
        Review existing = reviews.get(review.getReviewId());
        if (existing == null) {
            throw new NoSuchElementException("Отзыв не найден");
        }
        existing.setContent(review.getContent());
        existing.setIsPositive(review.getIsPositive());
        return existing;
    }

    @Override
    public void delete(Long id) {
        reviews.remove(id);
        reviewVotes.remove(id);
    }

    @Override
    public Optional<Review> getById(Long id) {
        return Optional.ofNullable(reviews.get(id));
    }

    @Override
    public List<Review> getAll(Long filmId, int count) {
        List<Review> result = new ArrayList<>();
        for (Review r : reviews.values()) {
            if (filmId == null || r.getFilmId().equals(filmId)) {
                result.add(r);
            }
        }
        result.sort((r1, r2) -> Integer.compare(r2.getUseful(), r1.getUseful()));
        return result.size() > count ? result.subList(0, count) : result;
    }

    private void vote(Long reviewId, Long userId, boolean useful) {
        reviewVotes.putIfAbsent(reviewId, new HashMap<>());
        Map<Long, Boolean> votes = reviewVotes.get(reviewId);
        if (votes.containsKey(userId)) {
            throw new IllegalStateException("Пользователь уже голосовал за этот отзыв");
        }
        votes.put(userId, useful);
        Review review = reviews.get(reviewId);
        review.setUseful(review.getUseful() + (useful ? 1 : -1));
    }

    private void removeVote(Long reviewId, Long userId, boolean useful) {
        Map<Long, Boolean> votes = reviewVotes.get(reviewId);
        if (votes == null || !votes.containsKey(userId) || votes.get(userId) != useful) {
            throw new IllegalStateException("Нет такого голоса для удаления");
        }
        votes.remove(userId);
        Review review = reviews.get(reviewId);
        review.setUseful(review.getUseful() + (useful ? -1 : 1));
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        vote(reviewId, userId, true);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        vote(reviewId, userId, false);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        removeVote(reviewId, userId, true);
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        removeVote(reviewId, userId, false);
    }
}
