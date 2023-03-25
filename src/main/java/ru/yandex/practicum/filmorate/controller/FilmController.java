package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController extends Controller<Film> {

    @Override
    @GetMapping
    public List<Film> getAll() {
        return super.getAll();
    }

    @Override
    @PostMapping
    public Film create(@RequestBody Film film) {
        return super.create(film);
    }

    @Override
    @PutMapping
    public Film update(@RequestBody Film film) {
        try {
            return super.update(film);
        } catch (ObjectNotFoundException e) {
            log.warn("Film update failed due to absence of film with such id");
            throw new ObjectNotFoundException("Film with such id not found");
        }
    }

    @Override
    void validate(Film film) {
        if (film == null) {
            log.warn("Validation failed due to empty film");
            throw new ValidationException("Film must not be empty");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Validation failed due to incorrect Film name");
            throw new ValidationException("Film name must not be empty");
        }
        if (film.getDescription() == null || film.getDescription().isBlank()) {
            log.warn("Validation failed due to incorrect Film description");
            throw new ValidationException("Film description must not be empty");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Validation failed due to incorrect Film description");
            throw new ValidationException("Film description must not be longer than 200 characters");
        }
        if (film.getReleaseDate() == null) {
            log.warn("Validation failed due to incorrect Film release date");
            throw new ValidationException("Film release date must not be empty");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Validation failed due to incorrect Film release date");
            throw new ValidationException("Film release date must not be earlier than 28.12.1895");
        }
        if (film.getDuration() <= 0) {
            log.warn("Validation failed due to incorrect Film duration");
            throw new ValidationException("Film duration must be greater than 0");
        }
    }

    @Override
    void logCreationInfo(Film film) {
        log.info("Film {} has been added to catalogue", film);
    }

    @Override
    void logUpdateInfo(Film film) {
        log.info("Film {} has been updated", film);
    }
}