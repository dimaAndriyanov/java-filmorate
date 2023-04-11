package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;

class InMemoryStorageTest extends StorageTest {
    @BeforeEach
    void setStorage() {
        setStorage(new InMemoryStorage<>());
    }
}