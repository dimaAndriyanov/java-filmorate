package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getAll();

    Film getById(int id);

    Film add(Film film);

    Film update(Film film);

    void deleteAll();

    Film deleteById(int id);

    List<Film> getPopular(int count);

    default List<Film> getPopular() {
        return getPopular(10);
    }

    void addToFilmsPopularity(Film film);

    void deleteFromFilmsPopularity(int id);

    void deleteAllLikesFromUserById(int id);
}