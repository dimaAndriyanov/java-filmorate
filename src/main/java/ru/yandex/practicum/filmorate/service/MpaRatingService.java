package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.MpaRatingDao;

import java.util.List;

@Service
public class MpaRatingService {
    private final MpaRatingDao mpaRatingDao;

    @Autowired
    public MpaRatingService(MpaRatingDao mpaRatingDao) {
        this.mpaRatingDao = mpaRatingDao;
    }

    public List<MpaRating> getAll() {
        return mpaRatingDao.getAll();
    }

    public MpaRating getById(int id) {
        return mpaRatingDao.getById(id);
    }
}