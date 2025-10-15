package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
@ActiveProfiles("test")
class UserDbStorageTest {

    private final UserDbStorage userStorage;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("test@email.com", "testLogin", "Test User",
                LocalDate.of(1990, 1, 1));
    }

    @Test
    void testCreateUser() {
        User createdUser = userStorage.create(testUser);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo("test@email.com");
        assertThat(createdUser.getLogin()).isEqualTo("testLogin");
        assertThat(createdUser.getName()).isEqualTo("Test User");
    }

    @Test
    void testUpdateUser() {
        User createdUser = userStorage.create(testUser);

        User updatedUser = new User("updated@email.com", "updatedLogin", "Updated User",
                LocalDate.of(1995, 1, 1));
        updatedUser.setId(createdUser.getId());

        User result = userStorage.update(updatedUser);

        assertThat(result.getEmail()).isEqualTo("updated@email.com");
        assertThat(result.getLogin()).isEqualTo("updatedLogin");
        assertThat(result.getName()).isEqualTo("Updated User");
    }

    @Test
    void testGetUserById() {
        User createdUser = userStorage.create(testUser);

        Optional<User> foundUser = userStorage.getById(createdUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(createdUser.getId());
        assertThat(foundUser.get().getEmail()).isEqualTo("test@email.com");
    }

    @Test
    void testGetUserByIdNotFound() {
        Optional<User> foundUser = userStorage.getById(999L);

        assertThat(foundUser).isEmpty();
    }

    @Test
    void testGetAllUsers() {
        User user1 = userStorage.create(testUser);

        User user2 = new User("test2@email.com", "testLogin2", "Test User 2",
                LocalDate.of(1992, 1, 1));
        userStorage.create(user2);

        List<User> users = userStorage.getAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getEmail)
                .containsExactlyInAnyOrder("test@email.com", "test2@email.com");
    }

    @Test
    void testDeleteUser() {
        User createdUser = userStorage.create(testUser);

        userStorage.delete(createdUser.getId());

        Optional<User> foundUser = userStorage.getById(createdUser.getId());

        assertThat(foundUser).isEmpty();
    }

    @Test
    void testCreateUserWithEmptyName() {
        User user = new User("empty@email.com", "emptyLogin", "",
                LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.create(user);

        assertThat(createdUser.getName()).isEqualTo("emptyLogin");
    }
}