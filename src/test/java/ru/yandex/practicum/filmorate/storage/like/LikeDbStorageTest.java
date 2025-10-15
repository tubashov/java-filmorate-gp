package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({LikeDbStorage.class, FilmDbStorage.class, UserDbStorage.class, FilmRowMapper.class, UserRowMapper.class})
@ActiveProfiles("test")
class LikeDbStorageTest {

    private final LikeDbStorage likeStorage;
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    private Film testFilm;
    private User testUser;

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

        filmStorage.create(testFilm);

        testUser = userStorage.create(new User("test@email.com", "testUser", "Test User",
                LocalDate.of(1990, 1, 1)));
    }

    @Test
    void testAddLike() {
        likeStorage.addLike(testFilm.getId(), testUser.getId());

        int likesCount = likeStorage.getLikesCount(testFilm.getId());

        assertThat(likesCount).isEqualTo(1);
    }

    @Test
    void testRemoveLike() {
        likeStorage.addLike(testFilm.getId(), testUser.getId());
        likeStorage.removeLike(testFilm.getId(), testUser.getId());

        int likesCount = likeStorage.getLikesCount(testFilm.getId());

        assertThat(likesCount).isZero();
    }

    @Test
    void testGetLikesCount() {
        likeStorage.addLike(testFilm.getId(), testUser.getId());

        User user2 = userStorage.create(new User("user2@email.com", "user2", "User Two",
                LocalDate.of(1991, 1, 1)));
        likeStorage.addLike(testFilm.getId(), user2.getId());

        int likesCount = likeStorage.getLikesCount(testFilm.getId());

        assertThat(likesCount).isEqualTo(2);
    }
}