package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

public interface FilmStorage extends Storage<Film> {
    List<Film> getPopular(int count);

    default List<Film> getPopular() {
        return getPopular(10);
    }

    void addToFilmsPopularity(Film film);

    void deleteFromFilmsPopularity(int id);
}