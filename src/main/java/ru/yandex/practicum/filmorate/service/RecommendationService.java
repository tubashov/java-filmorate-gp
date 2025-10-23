package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.recommendation.RecommendationDbStorage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RecommendationService {
    private final RecommendationDbStorage recommendationStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public RecommendationService(RecommendationDbStorage recommendationStorage,
                                 @Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.recommendationStorage = recommendationStorage;
        this.filmStorage = filmStorage;
    }

    public List<Film> getRecommendation(Long userId) {
        log.info("Получение рекомендаций для пользователя с id={}", userId);

        List<Long> filmIds = Optional.ofNullable(recommendationStorage.getRecommendation(userId))
                .orElse(Collections.emptyList());

        if (filmIds.isEmpty()) {
            log.debug("Для пользователя {} нет рекомендаций", userId);
            return Collections.emptyList();
        }

        List<Film> recommendedFilms = filmStorage.getByIds(filmIds);

        log.info("Пользователю {} рекомендовано {} фильмов", userId, recommendedFilms.size());
        return recommendedFilms;
    }
}
