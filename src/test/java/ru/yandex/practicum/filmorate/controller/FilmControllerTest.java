package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    @Test
    public void validateTest() {
        FilmController controller = new FilmController();
        LocalDate acceptableReleaseDate = LocalDate.of(2000, 1, 1);
        ValidationException e;

        e = assertThrows(ValidationException.class, () -> controller.validate(null));
        assertEquals("Film must not be empty", e.getMessage());

        Film nullNameFilm = new Film(null, "description", acceptableReleaseDate, 90);
        e = assertThrows(ValidationException.class, () -> controller.validate(nullNameFilm));
        assertEquals("Film name must not be empty", e.getMessage());

        Film emptyNameFilm = new Film("", "description", acceptableReleaseDate, 90);
        e = assertThrows(ValidationException.class, () -> controller.validate(emptyNameFilm));
        assertEquals("Film name must not be empty", e.getMessage());

        Film nullDescriptionFilm = new Film("name", null, acceptableReleaseDate, 90);
        e = assertThrows(ValidationException.class, () -> controller.validate(nullDescriptionFilm));
        assertEquals("Film description must not be empty", e.getMessage());

        Film emptyDescriptionFilm = new Film("name", "", acceptableReleaseDate, 90);
        e = assertThrows(ValidationException.class, () -> controller.validate(emptyDescriptionFilm));
        assertEquals("Film description must not be empty", e.getMessage());

        String longDescription = "x".repeat(201);
        Film longDescriptionFilm = new Film("name", longDescription, acceptableReleaseDate, 90);
        e = assertThrows(ValidationException.class, () -> controller.validate(longDescriptionFilm));
        assertEquals("Film description must not be longer than 200 characters", e.getMessage());

        Film nullReleaseDateFilm = new Film("name", "description", null, 90);
        e = assertThrows(ValidationException.class, () -> controller.validate(nullReleaseDateFilm));
        assertEquals("Film release date must not be empty", e.getMessage());

        LocalDate tooEarlyReleaseDate = LocalDate.of(1895, 12, 27);
        Film tooEarlyReleaseDateFilm = new Film("name", "description", tooEarlyReleaseDate, 90);
        e = assertThrows(ValidationException.class, () -> controller.validate(tooEarlyReleaseDateFilm));
        assertEquals("Film release date must not be earlier than 28.12.1895", e.getMessage());

        Film zeroDurationFilm = new Film("name", "description", acceptableReleaseDate, 0);
        e = assertThrows(ValidationException.class, () -> controller.validate(zeroDurationFilm));
        assertEquals("Film duration must be greater than 0", e.getMessage());

        Film acceptableFilm = new Film("name", "description", acceptableReleaseDate, 90);
        assertDoesNotThrow(() -> controller.validate(acceptableFilm));
    }
}