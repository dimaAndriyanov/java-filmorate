package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDaoImplTest {
    private final GenreDao genreDao;
    private final FilmDbStorage filmDbStorage;

    @Test
    void testGetAll() {
        List<Genre> genres = genreDao.getAll();
        assertEquals(6, genres.size());

        Genre testGenre = new Genre();

        testGenre.setId(1);
        testGenre.setName("Комедия");
        assertTrue(genres.contains(testGenre));

        testGenre.setId(2);
        testGenre.setName("Драма");
        assertTrue(genres.contains(testGenre));

        testGenre.setId(3);
        testGenre.setName("Мультфильм");
        assertTrue(genres.contains(testGenre));

        testGenre.setId(4);
        testGenre.setName("Триллер");
        assertTrue(genres.contains(testGenre));

        testGenre.setId(5);
        testGenre.setName("Документальный");
        assertTrue(genres.contains(testGenre));

        testGenre.setId(6);
        testGenre.setName("Боевик");
        assertTrue(genres.contains(testGenre));
    }

    @Test
    void testGetById() {
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> genreDao.getById(7));
        assertEquals("Genre with id 7 not found", exception.getMessage());

        Genre documentaryGenre = new Genre();
        documentaryGenre.setId(5);
        documentaryGenre.setName("Документальный");
        assertEquals(documentaryGenre, genreDao.getById(5));
    }

    @Test
    void testGetByFilmId() {
        List<Genre> notExistingFilmGenres = genreDao.getByFilmId(9999);
        assertTrue(notExistingFilmGenres.isEmpty());

        MpaRating mpa = new MpaRating();
        mpa.setId(1);
        Film filmWithNoGenres = new Film("name001", "desc001", LocalDate.of(2000, 1, 1), 1, mpa);
        int film001Id = filmDbStorage.add(filmWithNoGenres).getId();

        Genre dramaGenre = new Genre();
        dramaGenre.setId(2);
        Film dramaFilm = new Film("name002", "desc002", LocalDate.of(2000, 1, 2), 2, mpa);
        dramaFilm.addGenre(dramaGenre);
        int film002Id = filmDbStorage.add(dramaFilm).getId();

        Genre comedyGenre = new Genre();
        comedyGenre.setId(1);
        Film comedyDramaFilm = new Film("name003", "desc003", LocalDate.of(2000, 1, 3), 3, mpa);
        comedyDramaFilm.addGenre(dramaGenre);
        comedyDramaFilm.addGenre(comedyGenre);
        int film003Id = filmDbStorage.add(comedyDramaFilm).getId();

        comedyGenre.setName("Комедия");
        dramaGenre.setName("Драма");

        assertTrue(genreDao.getByFilmId(film001Id).isEmpty());

        List<Genre> dramaFilmsGenres = genreDao.getByFilmId(film002Id);
        assertEquals(1, dramaFilmsGenres.size());
        assertEquals(dramaGenre, dramaFilmsGenres.get(0));

        List<Genre> comedyDramaFilmsGenres = genreDao.getByFilmId(film003Id);
        assertEquals(2, comedyDramaFilmsGenres.size());
        assertTrue(comedyDramaFilmsGenres.contains(dramaGenre));
        assertTrue(comedyDramaFilmsGenres.contains(comedyGenre));
    }
}