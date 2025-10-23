package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;
    private final FeedService feedService;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("friendshipDbStorage") FriendshipStorage friendshipStorage,
                       FeedService feedService) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
        this.feedService = feedService;
    }

    public List<User> getAllUsers() {
        List<User> users = userStorage.getAll();
        loadFriendsForUsers(users);
        return users;
    }

    public User getUserById(Long id) {
        User user = userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
        loadFriendsForUser(user);
        return user;
    }

    public User createUser(User user) {
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        getUserById(user.getId());
        return userStorage.update(user);
    }

    public void addFriend(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);
        validateAddFriend(userId, friendId);
        log.info("Пользователь {} добавляет в друзья пользователя {}", userId, friendId);
        friendshipStorage.addFriend(userId, friendId);

        feedService.addEvent(new Event(
                null,
                userId,
                friendId,
                Event.EventType.FRIEND,
                Event.Operation.ADD,
                System.currentTimeMillis()
        ));

    }

    public void removeFriend(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);

        friendshipStorage.removeFriend(userId, friendId);

        feedService.addEvent(new Event(
                null,
                userId,
                friendId,
                Event.EventType.FRIEND,
                Event.Operation.REMOVE,
                System.currentTimeMillis()
        ));
    }

    public List<User> getFriends(Long userId) {
        getUserById(userId);

        Set<Long> friendsIds = friendshipStorage.getFriends(userId);

        return getUsersByIds(friendsIds);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        getUserById(userId);
        getUserById(otherId);

        Set<Long> commonFriendIds = friendshipStorage.getCommonFriends(userId, otherId);

        return getUsersByIds(commonFriendIds);
    }

    private void loadFriendsForUser(User user) {
        Set<Long> friendsIds = friendshipStorage.getFriends(user.getId());
        user.setFriends(friendsIds);
    }

    private void loadFriendsForUsers(List<User> users) {
        if (users.isEmpty()) return;

        Set<Long> userIds = users.stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        Map<Long, Set<Long>> friendsMap = friendshipStorage.getFriendsForUsers(userIds);

        for (User user : users) {
            user.setFriends(friendsMap.getOrDefault(user.getId(), Collections.emptySet()));
        }
    }

    private List<User> getUsersByIds(Set<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, User> usersMap = userStorage.getUsersByIds(userIds);

        return userIds.stream()
                .map(usersMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void deleteUserById(long id) {
        getUserById(id);
        userStorage.deleteUserById(id);
    }

    private void validateAddFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            log.warn("Пользователь {} пытается добавить самого себя в друзья", userId);
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }

        if (friendshipStorage.areFriends(userId, friendId)) {
            log.warn("Пользователи {} и {} уже являются друзьями", userId, friendId);
            throw new ValidationException("Пользователи уже являются друзьями");
        }
    }
}