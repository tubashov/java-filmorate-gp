package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    List<Genre> getAll();

    Optional<Genre> getById(Long id);

    Set<Long> getExistingGenreIds(Set<Long> genreIds);
}