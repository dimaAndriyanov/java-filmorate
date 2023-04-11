package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFilmStorageTest {
    InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();

    @BeforeEach
    void clearStorage() {
        filmStorage.deleteAll();
    }

    @Test
    void getPopular() {
        assertNotNull(filmStorage.getPopular(10));
        assertTrue(filmStorage.getPopular(10).isEmpty());

        Film filmWithThreeLikes = new Film("nameA", "descA", LocalDate.of(2000, 1, 1), 90);
        filmWithThreeLikes.addLikeFromUserId(1);
        filmWithThreeLikes.addLikeFromUserId(2);
        filmWithThreeLikes.addLikeFromUserId(3);
        filmStorage.add(filmWithThreeLikes);

        Film filmWithZeroLikes = new Film("nameB", "descB", LocalDate.of(1990, 2, 2), 80);
        filmStorage.add(filmWithZeroLikes);

        Film filmWithOneLike = new Film("nameC", "descC", LocalDate.of(1995, 3, 3), 70);
        filmWithOneLike.addLikeFromUserId(1);
        filmStorage.add(filmWithOneLike);

        Film anotherFilmWithThreeLikes = new Film("nameD", "descD", LocalDate.of(2010, 4, 4), 100);
        anotherFilmWithThreeLikes.addLikeFromUserId(1);
        anotherFilmWithThreeLikes.addLikeFromUserId(3);
        anotherFilmWithThreeLikes.addLikeFromUserId(5);
        filmStorage.add(anotherFilmWithThreeLikes);

        Film filmWithFiveLikes = new Film("nameE", "descE", LocalDate.of(2015, 5, 5), 95);
        filmWithFiveLikes.addLikeFromUserId(1);
        filmWithFiveLikes.addLikeFromUserId(2);
        filmWithFiveLikes.addLikeFromUserId(3);
        filmWithFiveLikes.addLikeFromUserId(4);
        filmWithFiveLikes.addLikeFromUserId(5);
        filmStorage.add(filmWithFiveLikes);

        List<Film> popularFilms = filmStorage.getPopular();
        assertEquals(5, popularFilms.size());
        assertEquals(popularFilms.get(0), filmWithFiveLikes);
        assertEquals(popularFilms.get(1), anotherFilmWithThreeLikes);
        assertEquals(popularFilms.get(2), filmWithThreeLikes);
        assertEquals(popularFilms.get(3), filmWithOneLike);
        assertEquals(popularFilms.get(4), filmWithZeroLikes);

        List<Film> topThreeFilms = filmStorage.getPopular(3);
        assertEquals(3, topThreeFilms.size());
        assertEquals(topThreeFilms.get(0), filmWithFiveLikes);
        assertEquals(topThreeFilms.get(1), anotherFilmWithThreeLikes);
        assertEquals(topThreeFilms.get(2), filmWithThreeLikes);
    }

    @Test
    void add() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> filmStorage.add(null));
        assertEquals("Can not add null film", exception.getMessage());

        Film filmWithThreeLikes = new Film("nameA", "descA", LocalDate.of(2000, 1, 1), 90);
        filmWithThreeLikes.addLikeFromUserId(1);
        filmWithThreeLikes.addLikeFromUserId(2);
        filmWithThreeLikes.addLikeFromUserId(3);
        filmStorage.add(filmWithThreeLikes);

        Film filmWithOneLike = new Film("nameB", "descB", LocalDate.of(1995, 3, 3), 70);
        filmWithOneLike.addLikeFromUserId(1);
        filmStorage.add(filmWithOneLike);

        List<Film> popularFilms = filmStorage.getPopular();
        assertEquals(2, popularFilms.size());
        assertEquals(popularFilms.get(0), filmWithThreeLikes);
        assertEquals(popularFilms.get(1), filmWithOneLike);
    }

    @Test
    void update() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> filmStorage.update(null));
        assertEquals("Can not update null film", exception.getMessage());

        Film filmWithThreeLikes = new Film("nameA", "descA", LocalDate.of(2000, 1, 1), 90);
        filmWithThreeLikes.addLikeFromUserId(1);
        filmWithThreeLikes.addLikeFromUserId(2);
        filmWithThreeLikes.addLikeFromUserId(3);
        filmStorage.add(filmWithThreeLikes);

        Film filmWithOneLike = new Film("nameB", "descB", LocalDate.of(1995, 2, 2), 80);
        filmWithOneLike.addLikeFromUserId(1);
        filmStorage.add(filmWithOneLike);

        List<Film> popularFilms = filmStorage.getPopular();
        assertEquals(2, popularFilms.size());
        assertEquals(popularFilms.get(0), filmWithThreeLikes);
        assertEquals(popularFilms.get(1), filmWithOneLike);

        Film anotherFilmWithOneLike = new Film("nameC", "descC", LocalDate.of(2005, 3, 3), 70);
        anotherFilmWithOneLike.setId(2);
        anotherFilmWithOneLike.addLikeFromUserId(2);
        filmStorage.update(anotherFilmWithOneLike);

        popularFilms = filmStorage.getPopular();
        assertEquals(2, popularFilms.size());
        assertEquals(popularFilms.get(0), filmWithThreeLikes);
        assertEquals(popularFilms.get(1), anotherFilmWithOneLike);

        Film filmWithFourLikes = new Film("nameC", "descC", LocalDate.of(2005, 3, 3), 70);
        filmWithFourLikes.setId(2);
        filmWithFourLikes.addLikeFromUserId(1);
        filmWithFourLikes.addLikeFromUserId(2);
        filmWithFourLikes.addLikeFromUserId(3);
        filmWithFourLikes.addLikeFromUserId(4);
        filmStorage.update(filmWithFourLikes);

        popularFilms = filmStorage.getPopular();
        assertEquals(2, popularFilms.size());
        assertEquals(popularFilms.get(0), filmWithFourLikes);
        assertEquals(popularFilms.get(1), filmWithThreeLikes);
    }

    @Test
    void deleteAll() {
        Film filmWithThreeLikes = new Film("nameA", "descA", LocalDate.of(2000, 1, 1), 90);
        filmWithThreeLikes.addLikeFromUserId(1);
        filmWithThreeLikes.addLikeFromUserId(2);
        filmWithThreeLikes.addLikeFromUserId(3);
        filmStorage.add(filmWithThreeLikes);

        Film filmWithOneLike = new Film("nameB", "descB", LocalDate.of(1995, 3, 3), 70);
        filmWithOneLike.addLikeFromUserId(1);
        filmStorage.add(filmWithOneLike);

        List<Film> popularFilms = filmStorage.getPopular();
        assertEquals(2, popularFilms.size());
        assertEquals(popularFilms.get(0), filmWithThreeLikes);
        assertEquals(popularFilms.get(1), filmWithOneLike);

        filmStorage.deleteAll();

        popularFilms = filmStorage.getPopular();
        assertNotNull(popularFilms);
        assertTrue(popularFilms.isEmpty());
    }

    @Test
    void deleteById() {
        Film filmWithThreeLikes = new Film("nameA", "descA", LocalDate.of(2000, 1, 1), 90);
        filmWithThreeLikes.addLikeFromUserId(1);
        filmWithThreeLikes.addLikeFromUserId(2);
        filmWithThreeLikes.addLikeFromUserId(3);
        filmStorage.add(filmWithThreeLikes);

        Film filmWithOneLike = new Film("nameB", "descB", LocalDate.of(1995, 3, 3), 70);
        filmWithOneLike.addLikeFromUserId(1);
        filmStorage.add(filmWithOneLike);

        List<Film> popularFilms = filmStorage.getPopular();
        assertEquals(2, popularFilms.size());
        assertEquals(popularFilms.get(0), filmWithThreeLikes);
        assertEquals(popularFilms.get(1), filmWithOneLike);

        filmStorage.deleteById(1);

        popularFilms = filmStorage.getPopular();
        assertEquals(1, popularFilms.size());
        assertEquals(popularFilms.get(0), filmWithOneLike);
    }
}