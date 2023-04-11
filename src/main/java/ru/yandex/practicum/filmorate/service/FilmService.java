package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class FilmService extends StorageService<Film> {
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        super(filmStorage);
        this.userStorage = userStorage;
    }

    public void addLikeByUser(int filmId, int userId) {
        Film film = storage.getById(filmId);
        User user = userStorage.getById(userId);
        ((FilmStorage) storage).deleteFromFilmsPopularity(filmId);
        film.addLikeFromUserId(user.getId());
        ((FilmStorage) storage).addToFilmsPopularity(film);
    }

    public void deleteLikeByUser(int filmId, int userId) {
        Film film = storage.getById(filmId);
        User user = userStorage.getById(userId);
        ((FilmStorage) storage).deleteFromFilmsPopularity(filmId);
        film.deleteLikeFromUserId(user.getId());
        ((FilmStorage) storage).addToFilmsPopularity(film);
    }

    public List<Film> getPopular(int count) {
        return ((FilmStorage) storage).getPopular(count);
    }

    public List<Film> getPopular() {
        return ((FilmStorage) storage).getPopular();
    }
}