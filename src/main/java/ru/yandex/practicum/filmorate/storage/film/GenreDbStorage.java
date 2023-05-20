package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDbStorage {
    List<Genre> getAll();

    Genre getById(int id);

    List<Genre> getByFilmId(int filmId);
}