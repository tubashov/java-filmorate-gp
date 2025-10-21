package ru.yandex.practicum.filmorate.model;

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

    private EventType eventType;

    private Operation operation;

    private Long timestamp;

    public enum EventType { REVIEW, FRIEND, LIKE }

    public enum Operation { ADD, REMOVE, UPDATE }
}
