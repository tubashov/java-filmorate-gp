package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class DirectorService {
    private final DirectorStorage directorStorage;

    public List<Director> findAll() {
        return directorStorage.findAll();
    }

    public Director findDirectorById(Long id) {
        return directorStorage.findDirectorById(id);
    }

    public Director create(Director newDirector) {
        return directorStorage.create(newDirector);
    }

    public Director update(Director updatedDirector) {
        return directorStorage.update(updatedDirector);
    }

    public void deleteById(Long id) {
        directorStorage.deleteById(id);
    }
}