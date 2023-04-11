package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Film;

import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import java.util.List;

@Validated
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;

    @Autowired
    public FilmController(FilmService service) {
        this.service = service;
    }

    @GetMapping
    public List<Film> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        return service.add(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return service.update(film);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@Positive @RequestParam(required = false) Integer count) {
        return count != null ? service.getPopular(count) : service.getPopular();
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeByUser(@PathVariable int id, @PathVariable int userId) {
        service.addLikeByUser(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLikeByUser(@PathVariable int id, @PathVariable int userId) {
        service.deleteLikeByUser(id, userId);
    }
}