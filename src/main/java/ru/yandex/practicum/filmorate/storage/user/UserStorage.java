package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    List<User> getAll();

    Optional<User> getById(Long id);

    User create(User user);

    User update(User user);

    void delete(Long id);

    void deleteUserById(Long id);

    Map<Long, User> getUsersByIds(Set<Long> userIds);
}