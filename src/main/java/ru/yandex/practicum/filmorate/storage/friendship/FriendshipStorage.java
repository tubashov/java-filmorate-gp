package ru.yandex.practicum.filmorate.storage.friendship;

import java.util.Map;
import java.util.Set;

public interface FriendshipStorage {
    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    Set<Long> getFriends(Long userId);

    Set<Long> getCommonFriends(Long userId, Long otherId);

    Map<Long, Set<Long>> getFriendsForUsers(Set<Long> userIds);

    void removeAllFriendshipsForUser(Long userId);
}
