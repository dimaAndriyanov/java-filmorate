package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmsLikesDao;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final FilmsLikesDao filmsLikesDao;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       FilmsLikesDao filmsLikesDao) {
        this.filmStorage = filmStorage;
        this.filmsLikesDao = filmsLikesDao;
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(int id) {
        return filmStorage.getById(id);
    }

    public Film add(Film film) {
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void addLikeByUser(int filmId, int userId) {
        filmsLikesDao.addLikeFromUser(filmId, userId);
    }

    public void deleteLikeByUser(int filmId, int userId) {
        filmsLikesDao.deleteLikeFromUser(filmId, userId);
    }

    public List<Film> getPopular(int count) {
        return filmStorage.getPopular(count);
    }

    public List<Film> getPopular() {
        return filmStorage.getPopular();
    }
}