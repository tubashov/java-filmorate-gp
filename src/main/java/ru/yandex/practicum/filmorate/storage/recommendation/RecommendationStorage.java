package ru.yandex.practicum.filmorate.storage.recommendation;

import java.util.List;

public interface RecommendationStorage {
    List<Long> getRecommendation(Long userId);
}
