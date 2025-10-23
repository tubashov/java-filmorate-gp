package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> findAll() {
        log.info("Запрошен список режиссеров");
        String sql = "SELECT * FROM directors ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Director(rs.getLong("id"), rs.getString("name")));
    }

    @Override
    public Director findDirectorById(Long id) {
        String sql = "SELECT * FROM directors WHERE id = ?";
        log.info("Поиск режиссера по id {}", id);
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                    new Director(rs.getLong("id"), rs.getString("name")), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Режиссер с id %d не найден", id));
        }
    }

    @Override
    public Director create(Director newDirector) {
        if (newDirector == null) {
            throw new ValidationException("Режиссер не может быть null");
        }
        String name = newDirector.getName();
        if (name == null || name.isEmpty() || name.trim().isEmpty()) {
            throw new ValidationException("Имя режиссера не может быть пустым");
        }

        String sql = "INSERT INTO directors (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, newDirector.getName());
            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        Director director = findDirectorById(id);
        log.info("Добавлен режиссер {} с id = {}", director.getName(), director.getId());
        return director;
    }

    @Override
    public Director update(Director updatedDirector) {
        if (updatedDirector == null) {
            throw new ValidationException("Режиссер не может быть null");
        }
        if (updatedDirector.getId() == null) {
            throw new ValidationException("ID режиссера не может быть null");
        }
        String name = updatedDirector.getName();
        if (name == null || name.isEmpty() || name.trim().isEmpty()) {
            throw new ValidationException("Имя режиссера не может быть пустым");
        }
        findDirectorById(updatedDirector.getId());

        String sql = "UPDATE directors SET name = ? WHERE id = ?";
        jdbcTemplate.update(sql, updatedDirector.getName(), updatedDirector.getId());

        log.info("Обновлен режиссер с id = {}", updatedDirector.getId());
        return findDirectorById(updatedDirector.getId());
    }

    @Override
    public boolean deleteById(Long id) {
        log.info("Удаление режиссера по id - {}", id);
        String sql = "DELETE FROM directors WHERE id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    @Override
    public void checkDirectors(Set<Long> directorsIds) {
        if (directorsIds == null || directorsIds.isEmpty()) {
            throw new NotFoundException("У переданных режиссеров не указаны id");
        }
        String sql = "SELECT COUNT(*) FROM directors WHERE id IN (:ids)";
        String ids = directorsIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM directors WHERE id IN (" + ids + ")",
                Integer.class);

        if (Objects.nonNull(count) && count != directorsIds.size()) {
            throw new ValidationException("Не все указанные режиссеры были найдены");
        }
    }
}