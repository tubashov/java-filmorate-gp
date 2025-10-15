package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDbStorage.class, GenreRowMapper.class})
@ActiveProfiles("test")
class GenreDbStorageTest {

    private final GenreDbStorage genreStorage;

    @Test
    void testGetAllGenres() {
        List<Genre> genres = genreStorage.getAll();

        assertThat(genres).hasSize(6);
        assertThat(genres).extracting(Genre::getName)
                .containsExactlyInAnyOrder("Комедия", "Драма", "Мультфильм",
                        "Триллер", "Документальный", "Боевик");
    }

    @Test
    void testGetGenreById() {
        Optional<Genre> genre = genreStorage.getById(1L);

        assertThat(genre).isPresent();
        assertThat(genre.get().getName()).isEqualTo("Комедия");
    }

    @Test
    void testGetGenreByIdNotFound() {
        Optional<Genre> genre = genreStorage.getById(999L);

        assertThat(genre).isEmpty();
    }

    @Test
    void testGenresOrderedById() {
        List<Genre> genres = genreStorage.getAll();

        assertThat(genres).extracting(Genre::getId)
                .containsExactly(1L, 2L, 3L, 4L, 5L, 6L);
    }
}