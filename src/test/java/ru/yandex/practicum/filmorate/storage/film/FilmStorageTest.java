package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotEqualLikesException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class FilmStorageTest {

    FilmStorage filmStorage;

    void setFilmStorage(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    List<Film> addThreeFilms() {
        return List.of(
                filmStorage.add(new Film("nameA", "descA", LocalDate.of(2000, 1, 1), 10, null)),
                filmStorage.add(new Film("nameB", "descB", LocalDate.of(2000, 2, 2), 20, null)),
                filmStorage.add(new Film("nameC", "descC", LocalDate.of(2000, 3, 3), 30, null))
        );
    }

    @Test
    void getAll() {
        List<Film> listOfAllFilms = filmStorage.getAll();
        assertNotNull(listOfAllFilms);
        assertTrue(listOfAllFilms.isEmpty());

        List<Film> films = addThreeFilms();
        listOfAllFilms = filmStorage.getAll();
        assertEquals(new HashSet<>(films), new HashSet<>(listOfAllFilms));
    }

    @Test
    void getById() {
        List<Film> films = addThreeFilms();
        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> filmStorage.getById(4));
        assertEquals("Film with id 4 not found", objectNotFoundException.getMessage());
        assertEquals(films.get(0), filmStorage.getById(1));
    }

    @Test
    void add() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> filmStorage.add(null));
        assertEquals("Can not add null film", nullPointerException.getMessage());

        List<Film> films = addThreeFilms();
        assertEquals(3, filmStorage.getAll().size());
        assertEquals(3, filmStorage.getPopular().size());

        assertEquals(1, films.get(0).getId());
        assertEquals(2, films.get(1).getId());
        assertEquals(3, films.get(2).getId());

        assertTrue(films.get(0).getLikesFromUsersIds().isEmpty());
    }

    @Test
    void update() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> filmStorage.update(null));
        assertEquals("Can not update null film", nullPointerException.getMessage());

        addThreeFilms();

        Film notFromStorageFilm = new Film("nameD", "descD", LocalDate.of(2000, 4, 4), 40, null);
        notFromStorageFilm.setId(4);
        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> filmStorage.update(notFromStorageFilm));
        assertEquals("Film with id 4 not found", objectNotFoundException.getMessage());

        Film filmWithDifferentLikes = new Film("nameE", "descE", LocalDate.of(2000, 5, 5), 50, null);
        filmWithDifferentLikes.setId(1);
        filmWithDifferentLikes.addLikeFromUserId(1);
        NotEqualLikesException notEqualLikesException = assertThrows(NotEqualLikesException.class,
                () -> filmStorage.update(filmWithDifferentLikes));
        assertEquals("Updated and original films must have equal likes", notEqualLikesException.getMessage());

        Film originalFilmWithIdOne = filmStorage.getById(1);
        Film updatedFilmWithIdOne = new Film("nameF", "descF", LocalDate.of(2000, 6, 6), 60, null);
        updatedFilmWithIdOne.setId(1);
        filmStorage.update(updatedFilmWithIdOne);
        assertEquals(updatedFilmWithIdOne, filmStorage.getById(1));
        assertFalse(filmStorage.getAll().contains(originalFilmWithIdOne));
        assertTrue(filmStorage.getPopular().contains(updatedFilmWithIdOne));
        assertFalse(filmStorage.getPopular().contains(originalFilmWithIdOne));
    }

    @Test
    void deleteAll() {
        addThreeFilms();
        assertFalse(filmStorage.getAll().isEmpty());
        assertFalse(filmStorage.getPopular().isEmpty());

        filmStorage.deleteAll();
        assertTrue(filmStorage.getAll().isEmpty());
        assertTrue(filmStorage.getPopular().isEmpty());
    }

    @Test
    void deleteById() {
        addThreeFilms();
        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> filmStorage.deleteById(4));
        assertEquals("Film with id 4 not found", objectNotFoundException.getMessage());

        Film deletedFilm = filmStorage.deleteById(1);
        assertEquals(2, filmStorage.getAll().size());
        assertEquals(2, filmStorage.getPopular().size());
        assertFalse(filmStorage.getAll().contains(deletedFilm));
        assertFalse(filmStorage.getPopular().contains(deletedFilm));
        assertThrows(ObjectNotFoundException.class, () -> filmStorage.getById(1));
    }

    @Test
    void getPopular() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> filmStorage.getPopular(0));
        assertEquals("Parameter count must be greater then 0", illegalArgumentException.getMessage());

        assertNotNull(filmStorage.getPopular());
        assertTrue(filmStorage.getPopular().isEmpty());

        Film filmWithThreeLikes = new Film("nameA", "descA", LocalDate.of(2000, 1, 1), 10, null);
        filmStorage.add(filmWithThreeLikes);
        filmStorage.deleteFromFilmsPopularity(1);
        filmWithThreeLikes.addLikeFromUserId(1);
        filmWithThreeLikes.addLikeFromUserId(2);
        filmWithThreeLikes.addLikeFromUserId(3);
        filmStorage.addToFilmsPopularity(filmWithThreeLikes);

        Film filmWithZeroLikes = new Film("nameB", "descB", LocalDate.of(2000, 2, 2), 20, null);
        filmStorage.add(filmWithZeroLikes);

        Film filmWithOneLike = new Film("nameC", "descC", LocalDate.of(2000, 3, 3), 30, null);
        filmStorage.add(filmWithOneLike);
        filmStorage.deleteFromFilmsPopularity(3);
        filmWithOneLike.addLikeFromUserId(1);
        filmStorage.addToFilmsPopularity(filmWithOneLike);

        Film anotherFilmWithThreeLikes = new Film("nameD", "descD", LocalDate.of(2000, 4, 4), 40, null);
        filmStorage.add(anotherFilmWithThreeLikes);
        filmStorage.deleteFromFilmsPopularity(4);
        anotherFilmWithThreeLikes.addLikeFromUserId(1);
        anotherFilmWithThreeLikes.addLikeFromUserId(3);
        anotherFilmWithThreeLikes.addLikeFromUserId(5);
        filmStorage.addToFilmsPopularity(anotherFilmWithThreeLikes);

        Film filmWithFiveLikes = new Film("nameE", "descE", LocalDate.of(2000, 5, 5), 50, null);
        filmStorage.add(filmWithFiveLikes);
        filmStorage.deleteFromFilmsPopularity(5);
        filmWithFiveLikes.addLikeFromUserId(1);
        filmWithFiveLikes.addLikeFromUserId(2);
        filmWithFiveLikes.addLikeFromUserId(3);
        filmWithFiveLikes.addLikeFromUserId(4);
        filmWithFiveLikes.addLikeFromUserId(5);
        filmStorage.addToFilmsPopularity(filmWithFiveLikes);

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

        Film updatedFilmWithOneLike = new Film("nameF", "descF", LocalDate.of(2000, 6, 6), 60, null);
        updatedFilmWithOneLike.setId(3);
        updatedFilmWithOneLike.addLikeFromUserId(1);
        filmStorage.update(updatedFilmWithOneLike);
        popularFilms = filmStorage.getPopular();
        assertEquals(popularFilms.get(0), filmWithFiveLikes);
        assertEquals(popularFilms.get(1), anotherFilmWithThreeLikes);
        assertEquals(popularFilms.get(2), filmWithThreeLikes);
        assertEquals(popularFilms.get(3), updatedFilmWithOneLike);
        assertEquals(popularFilms.get(4), filmWithZeroLikes);

        filmStorage.deleteById(4);
        popularFilms = filmStorage.getPopular();
        assertEquals(popularFilms.get(0), filmWithFiveLikes);
        assertEquals(popularFilms.get(1), filmWithThreeLikes);
        assertEquals(popularFilms.get(2), updatedFilmWithOneLike);
        assertEquals(popularFilms.get(3), filmWithZeroLikes);
    }

    @Test
    void deleteAllLikesFromUserById() {
        Film firstFilm = new Film("nameA", "descA", LocalDate.of(2000, 1, 1), 10, null);
        filmStorage.add(firstFilm);
        filmStorage.deleteFromFilmsPopularity(1);
        firstFilm.addLikeFromUserId(1);
        firstFilm.addLikeFromUserId(2);
        filmStorage.addToFilmsPopularity(firstFilm);

        Film secondFilm = new Film("nameB", "descB", LocalDate.of(2000, 2, 2), 20, null);
        filmStorage.add(secondFilm);
        filmStorage.deleteFromFilmsPopularity(2);
        secondFilm.addLikeFromUserId(1);
        filmStorage.addToFilmsPopularity(secondFilm);

        Film thirdFilm = new Film("nameC", "descC", LocalDate.of(2000, 3, 3), 30, null);
        filmStorage.add(thirdFilm);

        List<Film> popularFilms = filmStorage.getPopular();
        assertEquals(popularFilms.get(0), firstFilm);
        assertEquals(popularFilms.get(1), secondFilm);
        assertEquals(popularFilms.get(2), thirdFilm);

        filmStorage.deleteAllLikesFromUserById(1);

        popularFilms = filmStorage.getPopular();
        assertEquals(popularFilms.get(0), firstFilm);
        assertEquals(popularFilms.get(1), thirdFilm);
        assertEquals(popularFilms.get(2), secondFilm);
    }
}