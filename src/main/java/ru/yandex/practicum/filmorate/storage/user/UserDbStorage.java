package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM users";
        List<User> users = jdbcTemplate.query(sql, userRowMapper);
        loadFriendsForUsers(users);
        return users;
    }

    @Override
    public Optional<User> getById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, id);
        if (users.isEmpty()) {
            return Optional.empty();
        }
        User user = users.get(0);
        loadFriendsForUser(user);
        return Optional.of(user);
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return getById(user.getId()).orElseThrow();
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void deleteUserById(Long userId) {
        String deleteFriendshipsSql = "DELETE FROM friendships WHERE user_id = ? OR friend_id = ?";
        jdbcTemplate.update(deleteFriendshipsSql, userId, userId);

        String deleteLikesSql = "DELETE FROM film_likes WHERE user_id = ?";
        jdbcTemplate.update(deleteLikesSql, userId);

        String deleteReviewsSql = "DELETE FROM reviews WHERE user_id = ?";
        jdbcTemplate.update(deleteReviewsSql, userId);

        String deleteUserSql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(deleteUserSql, userId);
    }

    private void loadFriendsForUsers(List<User> users) {
        if (users.isEmpty()) return;

        String userIds = users.stream()
                .map(User::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String sql = String.format(
                "SELECT user_id, friend_id FROM friendships WHERE user_id IN (%s) AND status = 'CONFIRMED'",
                userIds
        );

        Map<Long, Set<Long>> userFriendsMap = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            Long userId = rs.getLong("user_id");
            Long friendId = rs.getLong("friend_id");
            userFriendsMap.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        });

        for (User user : users) {
            user.setFriends(userFriendsMap.getOrDefault(user.getId(), new HashSet<>()));
        }
    }

    private void loadFriendsForUser(User user) {
        String sql = "SELECT friend_id FROM friendships WHERE user_id = ? AND status = 'CONFIRMED'";
        Set<Long> friends = new HashSet<>(jdbcTemplate.query(sql,
                new Object[]{user.getId()},
                (rs, rowNum) -> rs.getLong("friend_id")));
        user.setFriends(friends);
    }

    @Override
    public Map<Long, User> getUsersByIds(Set<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String inClause = userIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String sql = "SELECT * FROM users WHERE id IN (" + inClause + ")";

        List<User> users = jdbcTemplate.query(sql, userRowMapper);

        return users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }
}