package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Long userId, Long friendId) {
        String sql = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public Set<Long> getFriends(Long userId) {
        String sql = "SELECT friend_id FROM friendships WHERE user_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql,
                new Object[]{userId},
                (rs, rowNum) -> rs.getLong("friend_id")));
    }

    @Override
    public Set<Long> getCommonFriends(Long userId, Long otherId) {
        String sql = "SELECT f1.friend_id " +
                "FROM friendships f1 " +
                "JOIN friendships f2 ON f1.friend_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql,
                new Object[]{userId, otherId},
                (rs, rowNum) -> rs.getLong("friend_id")));
    }

    @Override
    public Map<Long, Set<Long>> getFriendsForUsers(Set<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String inClause = userIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String sql = "SELECT user_id, friend_id FROM friendships WHERE user_id IN (" + inClause + ")";

        Map<Long, Set<Long>> friendsMap = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            Long userId = rs.getLong("user_id");
            Long friendId = rs.getLong("friend_id");
            friendsMap.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        });

        return friendsMap;
    }

    @Override
    public boolean areFriends(Long userId, Long friendId) {
        String sql = "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId);
        return count != null && count > 0;
    }


}