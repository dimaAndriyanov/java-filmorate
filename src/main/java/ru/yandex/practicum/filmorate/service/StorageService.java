package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.HasId;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

public class StorageService<T extends HasId> {
    final Storage<T> storage;

    public StorageService(Storage<T> storage) {
        this.storage = storage;
    }

    public List<T> getAll() {
        return storage.getAll();
    }

    public T getById(int id) {
        return storage.getById(id);
    }

    public T add(T value) {
        return storage.add(value);
    }

    public T update(T value) {
        return storage.update(value);
    }
}