package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.BeforeEach;

class InMemoryUserStorageTest extends UserStorageTest {
    @BeforeEach
    void setStorage() {
        setUserStorage(new InMemoryUserStorage());
    }
}