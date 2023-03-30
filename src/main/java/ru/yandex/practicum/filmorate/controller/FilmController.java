package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;

import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("films")
@Slf4j
public class FilmController extends Controller<Film> {

    @Override
    @GetMapping
    public List<Film> getAll() {
        return super.getAll();
    }

    @Override
    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return super.create(film);
    }

    @Override
    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        try {
            return super.update(film);
        } catch (ObjectNotFoundException e) {
            log.warn("Film update failed due to absence of film with such id");
            throw new ObjectNotFoundException("Film with such id not found");
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