package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.MpaRatingDbStorage;

import java.util.List;

@Service
public class MpaRatingService {
    private final MpaRatingDbStorage mpaRatingDbStorage;

    @Autowired
    public MpaRatingService(MpaRatingDbStorage mpaRatingDbStorage) {
        this.mpaRatingDbStorage = mpaRatingDbStorage;
    }

    public List<MpaRating> getAll() {
        return mpaRatingDbStorage.getAll();
    }

    public MpaRating getById(int id) {
        return mpaRatingDbStorage.getById(id);
    }
}