package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {
    FilmService service;

    @BeforeEach
    void setService() {
        UserStorage userStorage = new InMemoryUserStorage();
        userStorage.add(new User("emailA@mail.com", "loginA", LocalDate.of(2000, 1, 1)));
        userStorage.add(new User("emailB@mail.com", "loginB", LocalDate.of(2000, 1, 1)));
        userStorage.add(new User("emailC@mail.com", "loginC", LocalDate.of(2000, 1, 1)));
        userStorage.add(new User("emailD@mail.com", "loginD", LocalDate.of(2000, 1, 1)));
        userStorage.add(new User("emailE@mail.com", "loginE", LocalDate.of(2000, 1, 1)));

        service = new FilmService(new InMemoryFilmStorage(), userStorage);
        service.add(new Film("nameA", "descA", LocalDate.of(2000, 1, 1), 60));
        service.add(new Film("nameB", "descB", LocalDate.of(2000, 2, 2), 70));
        service.add(new Film("nameC", "descC", LocalDate.of(2000, 3, 3), 80));
        service.add(new Film("nameD", "descD", LocalDate.of(2000, 4, 4), 90));
        service.add(new Film("nameE", "descE", LocalDate.of(2000, 5, 5), 100));
    }

    @Test
    void addLikeByUser() {
        assertThrows(ObjectNotFoundException.class, () -> service.addLikeByUser(6, 1));
        assertThrows(ObjectNotFoundException.class, () -> service.addLikeByUser(1, 6));

        service.getAll().forEach(film -> assertTrue(film.getLikesFromUsersIds().isEmpty()));

        service.addLikeByUser(1, 1);
        assertEquals(service.getById(1).getLikesFromUsersIds(), Set.of(1));
        assertTrue(service.getById(2).getLikesFromUsersIds().isEmpty());
        assertTrue(service.getById(3).getLikesFromUsersIds().isEmpty());
        assertTrue(service.getById(4).getLikesFromUsersIds().isEmpty());
        assertTrue(service.getById(5).getLikesFromUsersIds().isEmpty());

        for (int i = 2; i < 6; i++) {
            service.addLikeByUser(i, 1);
        }
        service.getAll().forEach(film ->
                assertEquals(film.getLikesFromUsersIds(), Set.of(1)));

        service.addLikeByUser(5, 2);
        service.addLikeByUser(4, 2);
        service.addLikeByUser(3, 2);
        service.addLikeByUser(5, 3);
        service.addLikeByUser(4, 3);
        List<Film> topThreeFilms = service.getPopular(3);
        assertEquals(topThreeFilms.get(0), service.getById(5));
        assertEquals(topThreeFilms.get(1), service.getById(4));
        assertEquals(topThreeFilms.get(2), service.getById(3));
    }

    @Test
    void deleteLikeByUser() {
        assertThrows(ObjectNotFoundException.class, () -> service.deleteLikeByUser(6, 1));
        assertThrows(ObjectNotFoundException.class, () -> service.deleteLikeByUser(1, 6));

        for (int i = 1; i < 6; i++) {
            for (int j = 1; j < 6; j++) {
                service.addLikeByUser(i, j);
            }
        }
        service.getAll().forEach(film -> assertEquals(5, film.getLikesFromUsersIds().size()));

        service.deleteLikeByUser(5, 1);
        assertEquals(4, service.getById(5).getLikesFromUsersIds().size());
        assertFalse(service.getById(5).getLikesFromUsersIds().contains(1));
        assertEquals(5, service.getById(4).getLikesFromUsersIds().size());
        assertEquals(5, service.getById(3).getLikesFromUsersIds().size());
        assertEquals(5, service.getById(2).getLikesFromUsersIds().size());
        assertEquals(5, service.getById(1).getLikesFromUsersIds().size());

        service.addLikeByUser(5, 1);
        for (int i = 1; i < 6; i++) {
            for (int j = i; j < 6; j++) {
                service.deleteLikeByUser(i, j);
            }
        }

        List<Film> topFilms = service.getPopular();
        assertEquals(topFilms.get(0), service.getById(5));
        assertEquals(topFilms.get(1), service.getById(4));
        assertEquals(topFilms.get(2), service.getById(3));
        assertEquals(topFilms.get(3), service.getById(2));
        assertEquals(topFilms.get(4), service.getById(1));
    }
}