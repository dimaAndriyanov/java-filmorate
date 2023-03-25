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
        ValidationException e;

        e = assertThrows(ValidationException.class, () -> controller.validate(null));
        assertEquals("Film must not be empty", e.getMessage());

        LocalDate tooEarlyReleaseDate = LocalDate.of(1895, 12, 27);
        Film tooEarlyReleaseDateFilm = new Film("name", "description", tooEarlyReleaseDate, 90);
        e = assertThrows(ValidationException.class, () -> controller.validate(tooEarlyReleaseDateFilm));
        assertEquals("Film release date must not be earlier than 28.12.1895", e.getMessage());


        LocalDate acceptableReleaseDate = LocalDate.of(2000, 1, 1);
        Film acceptableFilm = new Film("name", "description", acceptableReleaseDate, 90);
        assertDoesNotThrow(() -> controller.validate(acceptableFilm));
    }
}