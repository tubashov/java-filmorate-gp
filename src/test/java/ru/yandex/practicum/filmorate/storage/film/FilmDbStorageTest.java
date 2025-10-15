package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class})
@ActiveProfiles("test")
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;
    private Film testFilm;

    @BeforeEach
    void setUp() {
        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Test Description");
        testFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilm.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1L);
        testFilm.setMpa(mpa);
    }

    @Test
    void testCreateFilm() {
        Film createdFilm = filmStorage.create(testFilm);

        assertThat(createdFilm).isNotNull();
        assertThat(createdFilm.getId()).isNotNull();
        assertThat(createdFilm.getName()).isEqualTo("Test Film");
        assertThat(createdFilm.getDescription()).isEqualTo("Test Description");
        assertThat(createdFilm.getDuration()).isEqualTo(120);
        assertThat(createdFilm.getMpa()).isNotNull();
        assertThat(createdFilm.getMpa().getId()).isEqualTo(1L);
    }

    @Test
    void testUpdateFilm() {
        Film createdFilm = filmStorage.create(testFilm);

        Film updatedFilm = new Film();
        updatedFilm.setId(createdFilm.getId());
        updatedFilm.setName("Updated Film");
        updatedFilm.setDescription("Updated Description");
        updatedFilm.setReleaseDate(LocalDate.of(2001, 1, 1));
        updatedFilm.setDuration(150);

        Mpa mpa = new Mpa();
        mpa.setId(2L);
        updatedFilm.setMpa(mpa);

        Film result = filmStorage.update(updatedFilm);

        assertThat(result.getName()).isEqualTo("Updated Film");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getDuration()).isEqualTo(150);
        assertThat(result.getMpa().getId()).isEqualTo(2L);
    }

    @Test
    void testGetFilmById() {
        Film createdFilm = filmStorage.create(testFilm);

        Optional<Film> foundFilm = filmStorage.getById(createdFilm.getId());

        assertThat(foundFilm).isPresent();
        assertThat(foundFilm.get().getId()).isEqualTo(createdFilm.getId());
        assertThat(foundFilm.get().getName()).isEqualTo("Test Film");
    }

    @Test
    void testGetFilmByIdNotFound() {
        Optional<Film> foundFilm = filmStorage.getById(999L);

        assertThat(foundFilm).isEmpty();
    }

    @Test
    void testGetAllFilms() {
        Film film1 = filmStorage.create(testFilm);

        Film film2 = new Film();
        film2.setName("Another Film");
        film2.setDescription("Another Description");
        film2.setReleaseDate(LocalDate.of(2002, 1, 1));
        film2.setDuration(90);

        Mpa mpa = new Mpa();
        mpa.setId(3L);
        film2.setMpa(mpa);

        filmStorage.create(film2);

        List<Film> films = filmStorage.getAll();

        assertThat(films).hasSize(2);
        assertThat(films).extracting(Film::getName)
                .containsExactlyInAnyOrder("Test Film", "Another Film");
    }

    @Test
    void testDeleteFilm() {
        Film createdFilm = filmStorage.create(testFilm);

        filmStorage.delete(createdFilm.getId());

        Optional<Film> foundFilm = filmStorage.getById(createdFilm.getId());

        assertThat(foundFilm).isEmpty();
    }
}