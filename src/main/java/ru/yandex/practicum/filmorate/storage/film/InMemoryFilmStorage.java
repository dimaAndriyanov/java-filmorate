package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage extends InMemoryStorage<Film> implements FilmStorage {
    private final TreeSet<Film> filmsPopularity = new TreeSet<>((film1, film2) ->
            film1.getLikesFromUsersIds().size() != film2.getLikesFromUsersIds().size() ?
                    film1.getLikesFromUsersIds().size() - film2.getLikesFromUsersIds().size() :
                    film1.getId() - film2.getId());

    @Override
    public List<Film> getPopular(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Parameter count must be greater then 0");
        }
        return filmsPopularity.descendingSet().stream().limit(count).collect(Collectors.toList());
    }

    @Override
    public Film getById(int id) {
        try {
            return super.getById(id);
        } catch (ObjectNotFoundException exception) {
            throw new ObjectNotFoundException(String.format("Film with %d id not found", id));
        }
    }

    @Override
    public Film add(Film film) {
        if (film == null) {
            throw new NullPointerException("Can not add null film");
        }
        Film result = super.add(film);
        filmsPopularity.add(result);
        log.info("New film with id {} has been added", result.getId());
        return result;
    }

    @Override
    public Film update(Film film) {
        if (film == null) {
            throw new NullPointerException("Can not update null film");
        }
        try {
            filmsPopularity.remove(getById(film.getId()));
            filmsPopularity.add(film);
            Film result = super.update(film);
            log.info("Film with id {} has been updated", result.getId());
            return result;
        } catch (ObjectNotFoundException exception) {
            throw new ObjectNotFoundException(String.format("Film with %d id not found", film.getId()));
        }
    }

    @Override
    public void deleteAll() {
        filmsPopularity.clear();
        log.info("FilmStorage has been cleared");
        super.deleteAll();
    }

    @Override
    public Film deleteById(int id) {
        try {
            filmsPopularity.remove(getById(id));
            Film result = super.deleteById(id);
            log.info("Film with id {} has been removed", result.getId());
            return result;
        } catch (ObjectNotFoundException exception) {
            throw new ObjectNotFoundException(String.format("Film with %d id not found", id));
        }
    }

    public void deleteFromFilmsPopularity(int id) {
        filmsPopularity.remove(getById(id));
    }

    public void addToFilmsPopularity(Film film) {
        filmsPopularity.add(film);
    }
}