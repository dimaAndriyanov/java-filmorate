package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

public interface MpaRatingDao {
    List<MpaRating> getAll();

    MpaRating getById(int id);
}