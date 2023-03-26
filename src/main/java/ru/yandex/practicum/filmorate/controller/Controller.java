package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.HasId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Controller<T extends HasId> {
    private int nextId = 1;
    private final Map<Integer, T> map = new HashMap<>();

    public List<T> getAll() {
        return new ArrayList<>(map.values());
    }

    public T create(T value) {
        value.setId(getNextId());
        map.put(value.getId(), value);
        logCreationInfo(value);
        return value;
    }

    public T update(T value) {
        if (map.containsKey(value.getId())) {
            map.put(value.getId(), value);
            logUpdateInfo(value);
            return value;
        } else {
            throw new ObjectNotFoundException();
        }
    }

    abstract void logCreationInfo(T value);

    abstract void logUpdateInfo(T value);

    private int getNextId() {
        return nextId++;
    }
}