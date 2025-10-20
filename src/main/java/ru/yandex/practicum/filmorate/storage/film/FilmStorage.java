package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> getAll();

    Optional<Film> getById(Long id);

    Film create(Film film);

    Film update(Film film);

    void delete(Long id);

    List<Film> searchFilms(String query, boolean searchByTitle, boolean searchByDirector);

    List<Film> getPopularFilmsByGenreAndYear(int count, Long genreId, Integer year);

    List<Film> getPopularFilmsByGenre(int count, Long genreId);

    List<Film> getPopularFilmsByYear(int count, Integer year);

    void deleteFilmById(Long filmId);
}