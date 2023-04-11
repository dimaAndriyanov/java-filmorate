package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.HasId;

import java.util.List;

public interface Storage<T extends HasId> {

    List<T> getAll();

    T getById(int id);

    T add(T value);

    T update(T value);

    void deleteAll();

    T deleteById(int id);
}