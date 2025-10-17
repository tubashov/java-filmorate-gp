package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Director {
    Long id;
    @NotBlank
    String name;

    public Director(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
