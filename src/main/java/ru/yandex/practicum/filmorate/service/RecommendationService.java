package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.recommendation.RecommendationDbStorage;

import java.util.ArrayList;
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
        List<Long> filmIds = recommendationStorage.getRecommendation(userId);
        List<Film> films = new ArrayList<>();
        for (Long filmId : filmIds) {
            Optional<Film> film = filmStorage.getById(filmId);
            if (film.isPresent()) {
                films.add(film.get());
            }
        }
        return films;
    }
}
