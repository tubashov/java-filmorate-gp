package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.log;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserService userService;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final DirectorStorage directorStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("likeDbStorage") LikeStorage likeStorage,
                       UserService userService,
                       MpaStorage mpaStorage,
                       GenreStorage genreStorage,
                       DirectorStorage directorStorage) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
        this.userService = userService;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.directorStorage = directorStorage;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public Film getFilmById(Long id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + id + " не найден"));
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        validateMpa(film);
        validateGenres(film);
        validateDirectors(film);
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        getFilmById(film.getId());
        validateFilm(film);
        validateMpa(film);
        validateGenres(film);
        validateDirectors(film);
        return filmStorage.update(film);
    }

    private void validateDirectors(Film film) {
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            Set<Long> directorIds = film.getDirectors().stream()
                    .map(Director::getId)
                    .collect(Collectors.toSet());
            directorStorage.checkDirectors(directorIds);
        }
    }


    public void addLike(Long filmId, Long userId) {
        getFilmById(filmId);
        userService.getUserById(userId);
        likeStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        getFilmById(filmId);
        userService.getUserById(userId);
        likeStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAll().stream()
                .sorted((f1, f2) -> Integer.compare(
                        f2.getLikes().size(),
                        f1.getLikes().size()
                ))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            FilmService.log.warn("Дата релиза {} раньше минимально допустимой {}", film.getReleaseDate(), MIN_RELEASE_DATE);
            throw new ValidationException("Дата релиза не может быть раньше " + MIN_RELEASE_DATE);
        }
    }

    private void validateMpa(Film film) {
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            mpaStorage.getById(film.getMpa().getId())
                    .orElseThrow(() -> new NotFoundException("MPA рейтинг с ID " + film.getMpa().getId() + " не найден"));
        }
    }

    private void validateGenres(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Long> genreIds = film.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());

            Set<Long> existingGenreIds = genreStorage.getExistingGenreIds(genreIds);

            if (existingGenreIds.size() != genreIds.size()) {
                Set<Long> missingGenreIds = new HashSet<>(genreIds);
                missingGenreIds.removeAll(existingGenreIds);
                throw new NotFoundException("Жанры с ID " + missingGenreIds + " не найдены");
            }
        }
    }

    public List<Film> getFilmsListByDirector(Long directorId, String sortBy) {
        log.info("Проверяем существование режиссера с ID: {}", directorId);
        directorStorage.findDirectorById(directorId);

        log.info("Получаем все фильмы и фильтруем по режиссеру с ID: {}", directorId);
        List<Film> allFilms = filmStorage.getAll();
        List<Film> directorFilms = allFilms.stream()
                .filter(film -> film.getDirectors().stream()
                        .anyMatch(director -> director.getId().equals(directorId)))
                .collect(Collectors.toList());

        log.info("Найдено {} фильмов режиссера. Сортируем по параметру: {}", directorFilms.size(), sortBy);
        if ("year".equals(sortBy)) {
            directorFilms.sort(Comparator.comparing(Film::getReleaseDate));
        } else if ("likes".equals(sortBy)) {
            directorFilms.sort((f1, f2) -> Integer.compare(
                    f2.getLikes().size(),
                    f1.getLikes().size()
            ));
        }

        return directorFilms;
    }
}