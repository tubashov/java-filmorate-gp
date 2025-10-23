package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class Review {

    @Positive
    private Long reviewId;

    @NotBlank(message = "Текст отзыва не может быть пустым")
    private String content;

    @NotNull(message = "Тип отзыва должен быть указан")
    private Boolean isPositive;

    @NotNull(message = "UserId не может быть null")
    private Long userId;

    @NotNull(message = "FilmId не может быть null")
    private Long filmId;

    private Integer useful; // Рейтинг полезности
}
