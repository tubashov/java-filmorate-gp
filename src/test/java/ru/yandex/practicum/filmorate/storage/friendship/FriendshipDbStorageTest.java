package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FriendshipDbStorage.class, UserDbStorage.class, UserRowMapper.class})
@ActiveProfiles("test")
class FriendshipDbStorageTest {

    private final FriendshipDbStorage friendshipStorage;
    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        user1 = userStorage.create(new User("user1@email.com", "user1", "User One",
                LocalDate.of(1990, 1, 1)));
        user2 = userStorage.create(new User("user2@email.com", "user2", "User Two",
                LocalDate.of(1991, 1, 1)));
        user3 = userStorage.create(new User("user3@email.com", "user3", "User Three",
                LocalDate.of(1992, 1, 1)));
    }

    @Test
    void testAddFriend() {
        friendshipStorage.addFriend(user1.getId(), user2.getId());

        Set<Long> friends = friendshipStorage.getFriends(user1.getId());

        assertThat(friends).hasSize(1);
        assertThat(friends).contains(user2.getId());
    }

    @Test
    void testRemoveFriend() {
        friendshipStorage.addFriend(user1.getId(), user2.getId());
        friendshipStorage.removeFriend(user1.getId(), user2.getId());

        Set<Long> friends = friendshipStorage.getFriends(user1.getId());

        assertThat(friends).isEmpty();
    }

    @Test
    void testGetFriends() {
        friendshipStorage.addFriend(user1.getId(), user2.getId());
        friendshipStorage.addFriend(user1.getId(), user3.getId());

        Set<Long> friends = friendshipStorage.getFriends(user1.getId());

        assertThat(friends).hasSize(2);
        assertThat(friends).containsExactlyInAnyOrder(user2.getId(), user3.getId());
    }

    @Test
    void testGetCommonFriends() {
        friendshipStorage.addFriend(user1.getId(), user2.getId());
        friendshipStorage.addFriend(user1.getId(), user3.getId());
        friendshipStorage.addFriend(user2.getId(), user3.getId());

        Set<Long> commonFriends = friendshipStorage.getCommonFriends(user1.getId(), user2.getId());

        assertThat(commonFriends).hasSize(1);
        assertThat(commonFriends).contains(user3.getId());
    }
}