package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Получение всех фильмов.");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("Получение фильма с ID: {}", id);
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(defaultValue = "10", required = false) Integer count,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Integer year) {

        if (genreId != null && year != null) {
            log.info("Получение {} популярных фильмов жанра ID:{} за {} год", count, genreId, year);
            return filmService.getPopularFilmsByGenreAndYear(count, genreId, year);
        } else if (genreId != null) {
            log.info("Получение {} популярных фильмов жанра ID:{}", count, genreId);
            return filmService.getPopularFilmsByGenre(count, genreId);
        } else if (year != null) {
            log.info("Получение {} популярных фильмов за {} год", count, year);
            return filmService.getPopularFilmsByYear(count, year);
        } else {
            log.info("Получение {} популярных фильмов", count);
            return filmService.getPopularFilms(count);
        }
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Создание нового фильма: {}", film);
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Обновление фильма: {}", film);
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь {} ставит лайк фильму {}", userId, id);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь {} удаляет лайк фильма {}", userId, id);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam("userId") Long userId,
                                     @RequestParam("friendId") Long friendId) {
        log.info("Получение общих фильмов для пользователей {} и {}", userId, friendId);
        return filmService.getCommonLikedFilms(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsListByDirectorSortedByLikesOrYear(@PathVariable Long directorId,
                                                                @RequestParam String sortBy) {
        return filmService.getFilmsListByDirector(directorId, sortBy);
    }

    //поиск фильма
    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam(required = false) String query,
                                  @RequestParam(defaultValue = "title,director") String by) {
        log.info("Поиск фильмов по запросу: '{}', параметры: {}", query, by);
        return filmService.searchFilms(query, by);
    }


}
