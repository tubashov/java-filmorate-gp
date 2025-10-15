package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaDbStorage.class, MpaRowMapper.class})
@ActiveProfiles("test")
class MpaDbStorageTest {

    private final MpaDbStorage mpaStorage;

    @Test
    void testGetAllMpa() {
        List<Mpa> mpaList = mpaStorage.getAll();

        assertThat(mpaList).hasSize(5);
        assertThat(mpaList).extracting(Mpa::getName)
                .containsExactlyInAnyOrder("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    void testGetMpaById() {
        Optional<Mpa> mpa = mpaStorage.getById(1L);

        assertThat(mpa).isPresent();
        assertThat(mpa.get().getName()).isEqualTo("G");
    }

    @Test
    void testGetMpaByIdNotFound() {
        Optional<Mpa> mpa = mpaStorage.getById(999L);

        assertThat(mpa).isEmpty();
    }

    @Test
    void testMpaOrderedById() {
        List<Mpa> mpaList = mpaStorage.getAll();

        assertThat(mpaList).extracting(Mpa::getId)
                .containsExactly(1L, 2L, 3L, 4L, 5L);
    }
}