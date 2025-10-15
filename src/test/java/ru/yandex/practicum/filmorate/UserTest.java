package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class UserTest {

    @Autowired
    private UserController userController;

    private User user;

    private Validator validator;

    @BeforeEach
    void setUp() {
        user = createTestUser();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        user.setEmail(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        user.setEmail("        ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        user.setEmail("email-without-at");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenLoginIsNull() {
        user.setLogin(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenLoginIsBlank() {
        user.setLogin("      ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenLoginContainsSpaces() {
        user.setLogin("login with spaces");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenBirthdayIsInFuture() {
        user.setBirthday(LocalDate.now().plusDays(2));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    private User createTestUser() {
        return new User("test@yandex.ru", "testUserLogin", "testUserName",
                LocalDate.of(1996, 7, 31));
    }
}