package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Set;

public interface DirectorStorage {

    List<Director> findAll();

    Director findDirectorById(Long id);

    Director create(Director newDirector);

    Director update(Director updatedDirector);

    boolean deleteById(Long id);

    void checkDirectors(Set<Long> directorsIds);
}