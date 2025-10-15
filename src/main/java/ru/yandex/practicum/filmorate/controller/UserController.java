package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

/**
 * {@code GET /users} - Получение пользователей<br/>
 * {@code GET /users/{id}} - Получение пользователя<br/>
 * {@code GET /users/{id}/friends} - Получение друзей пользователя<br/>
 * {@code GET /users/{id}/friends/common/{otherId}} - Получение общих друзей пользователей<br/>
 * {@code POST /users} - Создание пользователя<br/>
 * {@code PUT /users} - Редактирование пользователя<br/>
 * {@code PUT /users/{id}/friends/{friendId}} - Добавление в друзья<br/>
 * {@code DELETE /users/{id}/friends/{friendId}} - Удаление из друзей<br/>
 */

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Получение всех пользователей.");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserByID(@PathVariable Long id) {
        log.info("Получение пользователя с ID: {}", id);
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("Получение списка друзей пользователя с ID: {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получение общий друзей пользователя с ID: {} и пользователя с ID: {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Создание пользователя: {}", user);
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Обновление пользователя: {}", user);
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователь с ID: {} добавляет в друзья пользователя с ID: {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователь с ID: {} удаляет из друзей пользователя с ID: {}", id, friendId);
        userService.removeFriend(id, friendId);
    }
}
