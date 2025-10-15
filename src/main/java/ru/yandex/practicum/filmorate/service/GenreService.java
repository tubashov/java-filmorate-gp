package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Genre> getAllGenres() {
        log.info("Получение всех жанров");
        return genreStorage.getAll();
    }

    public Genre getGenreById(Long id) {
        log.info("Получение жанра с ID: {}", id);
        return genreStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с ID " + id + " не найден"));
    }
}