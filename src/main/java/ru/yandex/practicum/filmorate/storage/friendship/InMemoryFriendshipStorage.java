package ru.yandex.practicum.filmorate.storage.friendship;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InMemoryFriendshipStorage implements FriendshipStorage {
    private final Map<Long, Set<Long>> friends = new HashMap<>();

    @Override
    public void addFriend(Long userId, Long friendId) {
        friends.computeIfAbsent(userId, s -> new HashSet<>()).add(friendId);
        friends.computeIfAbsent(friendId, s -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        removeFromFriends(userId, friendId);
        removeFromFriends(friendId, userId);
    }

    @Override
    public Set<Long> getFriends(Long userId) {
        return friends.getOrDefault(userId, Collections.emptySet());
    }

    @Override
    public Set<Long> getCommonFriends(Long userId, Long otherId) {
        Set<Long> userFriends = getFriends(userId);
        Set<Long> otherFriends = getFriends(otherId);

        Set<Long> commonFriends = new HashSet<>(userFriends);
        commonFriends.retainAll(otherFriends);

        return commonFriends;
    }

    @Override
    public Map<Long, Set<Long>> getFriendsForUsers(Set<Long> userIds) {
        return Map.of();
    }

    private void removeFromFriends(Long from, Long to) {
        if (friends.containsKey(from)) {
            friends.get(from).remove(to);
        }
    }
}