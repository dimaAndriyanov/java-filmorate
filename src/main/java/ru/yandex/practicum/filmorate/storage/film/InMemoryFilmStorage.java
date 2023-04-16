package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotEqualLikesException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;


import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private int nextId = 1;
    private final Map<Integer, Film> films = new HashMap<>();
    private final TreeSet<Film> filmsPopularity = new TreeSet<>((film1, film2) ->
            film1.getLikesFromUsersIds().size() != film2.getLikesFromUsersIds().size() ?
                    film1.getLikesFromUsersIds().size() - film2.getLikesFromUsersIds().size() :
                    film1.getId() - film2.getId());

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getById(int id) {
        Film result = films.get(id);
        if (result != null) {
            return result;
        } else {
            throw new ObjectNotFoundException(String.format("Film with id %d not found", id));
        }
    }

    @Override
    public Film add(Film film) {
        if (film == null) {
            throw new NullPointerException("Can not add null film");
        }
        film.setId(getNextId());
        film.deleteAllLikes();
        films.put(film.getId(), film);
        filmsPopularity.add(film);
        log.info("New film with id {} has been added", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (film == null) {
            throw new NullPointerException("Can not update null film");
        }
        if (!films.containsKey(film.getId())) {
            throw new ObjectNotFoundException(String.format("Film with id %d not found", film.getId()));
        }
        if (!film.getLikesFromUsersIds().equals(films.get(film.getId()).getLikesFromUsersIds())) {
            throw new NotEqualLikesException("Updated and original films must have equal likes");
        }
        filmsPopularity.remove(getById(film.getId()));
        filmsPopularity.add(film);
        films.put(film.getId(), film);
        log.info("Film with id {} has been updated", film.getId());
        return film;
    }

    @Override
    public void deleteAll() {
        films.clear();
        filmsPopularity.clear();
        log.info("FilmStorage has been cleared");
    }

    @Override
    public Film deleteById(int id) {
        Film film = films.remove(id);
        if (film != null) {
            filmsPopularity.remove(film);
            log.info("Film with id {} has been removed", film.getId());
            return film;
        } else {
            throw new ObjectNotFoundException(String.format("Film with id %d not found", id));
        }
    }

    @Override
    public List<Film> getPopular(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Parameter count must be greater then 0");
        }
        return filmsPopularity.descendingSet().stream().limit(count).collect(Collectors.toList());
    }

    @Override
    public void deleteFromFilmsPopularity(int id) {
        filmsPopularity.remove(getById(id));
    }

    @Override
    public void addToFilmsPopularity(Film film) {
        filmsPopularity.add(film);
    }

    @Override
    public void deleteAllLikesFromUserById(int id) {
        films.values().forEach(film -> film.deleteLikeFromUserId(id));
        filmsPopularity.clear();
        filmsPopularity.addAll(films.values());
    }

    private int getNextId() {
        return nextId++;
    }
}