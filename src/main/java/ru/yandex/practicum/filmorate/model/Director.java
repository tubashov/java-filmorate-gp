package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Director {
    @Positive
    private Long id;
    @NotBlank
    private String name;

    public Director(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
