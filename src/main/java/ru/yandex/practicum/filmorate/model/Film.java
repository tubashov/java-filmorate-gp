package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина символов: 200 символов")
    private String description;

    @NotNull(message = "Должна быть указана дата релиза фильма")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;

    private Mpa mpa;
    private Set<Genre> genres = new LinkedHashSet<>();
    private Set<Long> likes = new LinkedHashSet<>();
    private List<Director> directors;

}