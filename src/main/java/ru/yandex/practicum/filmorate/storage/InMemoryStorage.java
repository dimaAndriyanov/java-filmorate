package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.HasId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryStorage<T extends HasId> implements Storage<T> {

    private final Map<Integer, T> map = new HashMap<>();
    private int nextId = 1;

    @Override
    public List<T> getAll() {
        return new ArrayList<>(map.values());
    }

    @Override
    public T getById(int id) {
        T result = map.get(id);
        if (result != null) {
            return result;
        } else {
            throw new ObjectNotFoundException(String.format("Object with %d id not found", id));
        }
    }

    @Override
    public T add(T value) {
        if (value == null) {
            throw new NullPointerException("Can not add null value");
        }
        value.setId(getNextId());
        map.put(value.getId(), value);
        return value;
    }

    @Override
    public T update(T value) {
        if (value == null) {
            throw new NullPointerException("Can not update null value");
        }
        if (map.containsKey(value.getId())) {
            map.put(value.getId(), value);
            return value;
        } else {
            throw new ObjectNotFoundException(String.format("Object with %d id not found", value.getId()));
        }
    }

    @Override
    public void deleteAll() {
        map.clear();
    }

    @Override
    public T deleteById(int id) {
        T value = map.remove(id);
        if (value != null) {
            return value;
        } else {
            throw new ObjectNotFoundException(String.format("Object with %d id not found", id));
        }
    }

    private int getNextId() {
        return nextId++;
    }
}