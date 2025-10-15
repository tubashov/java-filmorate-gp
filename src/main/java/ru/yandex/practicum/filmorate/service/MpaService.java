package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public List<Mpa> getAllMpa() {
        log.info("Получение всех рейтингов MPA");
        return mpaStorage.getAll();
    }

    public Mpa getMpaById(Long id) {
        log.info("Получение рейтинга MPA с ID: {}", id);
        return mpaStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг MPA с ID " + id + " не найден"));
    }
}