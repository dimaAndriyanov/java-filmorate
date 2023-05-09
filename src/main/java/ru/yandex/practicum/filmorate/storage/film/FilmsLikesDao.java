package ru.yandex.practicum.filmorate.storage.film;

public interface FilmsLikesDao {
    void addLikeFromUser(int filmId, int userId);

    void deleteLikeFromUser(int filmId, int userId);
}