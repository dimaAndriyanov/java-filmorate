package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.BeforeEach;

class InMemoryFilmStorageTest extends FilmStorageTest {
    @BeforeEach
    void setStorage() {
        setFilmStorage(new InMemoryFilmStorage());
    }
}