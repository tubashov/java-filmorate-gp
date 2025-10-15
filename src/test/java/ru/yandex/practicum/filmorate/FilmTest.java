package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class FilmTest {
    @Autowired
    private FilmController filmController;

    private Film film;
    private Validator validator;

    @BeforeEach
    void createFilm() {
        film = createTestFilm();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidFilm() {
        Film createdFilm = filmController.createFilm(film);

        Assertions.assertNotNull(createdFilm.getId());
        Assertions.assertEquals(film.getName(), createdFilm.getName());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        film.setName(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        film.setName("");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsTooLong() {
        film.setDescription("a".repeat(201));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldAcceptDescriptionWhenDescriptionIs200() {
        film.setDescription("a".repeat(200));

        Assertions.assertDoesNotThrow(() -> filmController.createFilm(film));
    }

    @Test
    void shouldThrowExceptionWhenReleaseDateIsTooEarly() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        Assertions.assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void shouldAcceptMinimumReleaseDate() {
        film.setReleaseDate(LocalDate.of(1895, 12, 28));

        Assertions.assertDoesNotThrow(() -> filmController.createFilm(film));
    }

    @Test
    void shouldThrowExceptionWhenDurationIsNegative() {
        film.setDuration(-1);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenDurationIsZero() {
        film.setDuration(0);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldAcceptDurationWhenItIsOne() {
        film.setDuration(1);

        Assertions.assertDoesNotThrow(() -> filmController.createFilm(film));
    }

    private Film createTestFilm() {
        Film film = new Film();
        film.setName("TestFilm");
        film.setDescription("TestDescription");
        film.setReleaseDate(LocalDate.of(2007, 7, 7));
        film.setDuration(120);
        return film;
    }
}