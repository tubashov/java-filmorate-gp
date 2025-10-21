package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    private Long eventId;

    private Long userId;

    private Long entityId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private EventType eventType;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Operation operation;

    private Long timestamp;

    public enum EventType { REVIEW, FRIEND, LIKE }

    public enum Operation { ADD, REMOVE, UPDATE }
}
