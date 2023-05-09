package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotEqualLikesException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final MpaRatingDao mpaRatingDao;
    private final GenreDao genreDao;
    private final UserDbStorage userStorage;
    private final FilmsLikesDao filmsLikesDao;
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void filmDbStorageTest() {
        filmStorage.deleteAll();

        assertTrue(filmStorage.getAll().isEmpty());
        assertTrue(filmStorage.getPopular().isEmpty());

        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> filmStorage.getById(9999));
        assertEquals("Film with id 9999 not found", objectNotFoundException.getMessage());

        MpaRating mpa01 = new MpaRating();
        mpa01.setId(1);
        MpaRating mpa02 = new MpaRating();
        mpa02.setId(2);
        MpaRating mpa03 = new MpaRating();
        mpa03.setId(3);

        Genre genre01 = new Genre();
        genre01.setId(1);
        Genre genre02 = new Genre();
        genre02.setId(2);
        Genre genre03 = new Genre();
        genre03.setId(3);

        Film film021 = new Film("name021", "desc021", LocalDate.of(2000, 3, 1), 21, mpa01);
        film021.addLikeFromUserId(9999);
        Film film022 = new Film("name022", "desc022", LocalDate.of(2000, 3, 2), 22, mpa02);
        film022.addGenre(genre01);
        Film film023 = new Film("name023", "desc023", LocalDate.of(2000, 3, 3), 23, mpa03);
        film023.addGenre(genre02);
        film023.addGenre(genre03);

        film021 = filmStorage.add(film021);
        film022 = filmStorage.add(film022);
        film023 = filmStorage.add(film023);

        List<Film> films = filmStorage.getAll();
        assertEquals(3, films.size());
        assertTrue(films.contains(film021));
        assertTrue(films.contains(film022));
        assertTrue(films.contains(film023));

        assertEquals(film022, filmStorage.getById(film022.getId()));

        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> filmStorage.add(null));
        assertEquals("Can not add null film", nullPointerException.getMessage());

        assertTrue(filmStorage.getById(film021.getId()).getLikesFromUsersIds().isEmpty());

        assertTrue(filmStorage.getById(film021.getId()).getGenres().isEmpty());

        assertEquals(1, filmStorage.getById(film022.getId()).getGenres().size());
        assertTrue(filmStorage.getById(film022.getId()).getGenres().contains(genreDao.getById(1)));

        assertEquals(mpaRatingDao.getById(2), filmStorage.getById(film022.getId()).getMpa());

        assertEquals(2, filmStorage.getById(film023.getId()).getGenres().size());
        assertTrue(filmStorage.getById(film023.getId()).getGenres().contains(genreDao.getById(2)));
        assertTrue(filmStorage.getById(film023.getId()).getGenres().contains(genreDao.getById(3)));

        nullPointerException = assertThrows(NullPointerException.class, () -> filmStorage.update(null));
        assertEquals("Can not update null film", nullPointerException.getMessage());

        Film notExistingFilm = new Film("name024", "desc024", LocalDate.of(2000, 3, 4), 24, mpa03);
        notExistingFilm.setId(9999);
        objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> filmStorage.update(notExistingFilm));
        assertEquals("Film with id 9999 not found", objectNotFoundException.getMessage());

        Film filmWithDifferentLikes = new Film("name025", "desc025", LocalDate.of(2000, 3, 5), 25, mpa03);
        filmWithDifferentLikes.setId(film021.getId());
        filmWithDifferentLikes.addLikeFromUserId(9999);
        NotEqualLikesException notEqualLikesException = assertThrows(NotEqualLikesException.class,
                () -> filmStorage.update(filmWithDifferentLikes));
        assertEquals("Updated and original films must have equal likes", notEqualLikesException.getMessage());

        Film updatedFilm021 = new Film("name025", "desc025", LocalDate.of(2000, 3, 5), 25, mpa01);
        updatedFilm021.setId(film021.getId());
        updatedFilm021.addGenre(genre03);
        updatedFilm021.addGenre(genre02);

        Film updatedFilm022 = new Film("name026", "desc026", LocalDate.of(2000, 3, 6), 26, mpa01);
        updatedFilm022.setId(film022.getId());

        Film updatedFilm023 = new Film("name027", "desc027", LocalDate.of(2000, 3, 7), 27, mpa03);
        updatedFilm023.addGenre(genre01);
        updatedFilm023.setId(film023.getId());

        filmStorage.update(updatedFilm021);
        filmStorage.update(updatedFilm022);
        filmStorage.update(updatedFilm023);

        assertEquals(2, filmStorage.getById(film021.getId()).getGenres().size());
        assertTrue(filmStorage.getById(film021.getId()).getGenres().contains(genreDao.getById(2)));
        assertTrue(filmStorage.getById(film021.getId()).getGenres().contains(genreDao.getById(3)));

        assertTrue(filmStorage.getById(film022.getId()).getGenres().isEmpty());
        assertEquals(mpaRatingDao.getById(1), filmStorage.getById(film022.getId()).getMpa());

        assertEquals(1, filmStorage.getById(film023.getId()).getGenres().size());
        assertTrue(filmStorage.getById(film023.getId()).getGenres().contains(genreDao.getById(1)));

        User user021 = userStorage.add(new User("e021@mail.ru", "login021", LocalDate.of(1990, 3, 1)));
        User user022 = userStorage.add(new User("e022@mail.ru", "login022", LocalDate.of(1990, 3, 2)));
        User user023 = userStorage.add(new User("e023@mail.ru", "login023", LocalDate.of(1990, 3, 3)));

        filmsLikesDao.addLikeFromUser(film022.getId(), user021.getId());
        filmsLikesDao.addLikeFromUser(film022.getId(), user022.getId());
        filmsLikesDao.addLikeFromUser(film022.getId(), user023.getId());

        filmsLikesDao.addLikeFromUser(film023.getId(), user021.getId());
        filmsLikesDao.addLikeFromUser(film023.getId(), user022.getId());

        films = filmStorage.getPopular();

        assertEquals(3, films.size());
        assertEquals(filmStorage.getById(film022.getId()), films.get(0));
        assertEquals(filmStorage.getById(film023.getId()), films.get(1));
        assertEquals(filmStorage.getById(film021.getId()), films.get(2));

        filmsLikesDao.deleteLikeFromUser(film022.getId(), user021.getId());
        filmsLikesDao.deleteLikeFromUser(film022.getId(), user022.getId());

        films = filmStorage.getPopular();

        assertEquals(3, films.size());
        assertEquals(filmStorage.getById(film023.getId()), films.get(0));
        assertEquals(filmStorage.getById(film022.getId()), films.get(1));
        assertEquals(filmStorage.getById(film021.getId()), films.get(2));

        films = filmStorage.getPopular(1);
        assertEquals(1, films.size());
        assertEquals(filmStorage.getById(film023.getId()), films.get(0));

        objectNotFoundException = assertThrows(ObjectNotFoundException.class, () -> filmStorage.deleteById(9999));
        assertEquals("Film with id 9999 not found", objectNotFoundException.getMessage());

        assertEquals(2, jdbcTemplate.query("select user_id from films_likes where film_id = ?",
                (rs, rn) -> rs.getInt("user_id"), film023.getId()).size());

        assertEquals(1, jdbcTemplate.query("select genre_id from films_genres where film_id = ?",
                (rs, rn) -> rs.getInt("genre_id"), film023.getId()).size());

        film023 = filmStorage.deleteById(film023.getId());

        assertTrue(jdbcTemplate.query("select user_id from films_likes where film_id = ?",
                (rs, rn) -> rs.getInt("user_id"), film023.getId()).isEmpty());

        assertTrue(jdbcTemplate.query("select genre_id from films_genres where film_id = ?",
                (rs, rn) -> rs.getInt("genre_id"), film023.getId()).isEmpty());

        assertEquals(2, filmStorage.getAll().size());
        assertFalse(filmStorage.getAll().contains(film023));
        int film023Id = film023.getId();
        assertThrows(ObjectNotFoundException.class, () -> filmStorage.getById(film023Id));

        filmStorage.deleteAll();
        assertTrue(filmStorage.getAll().isEmpty());
        assertTrue(jdbcTemplate.query("select * from films_likes", (rs, ns) -> rs.getInt("user_id")).isEmpty());
        assertTrue(jdbcTemplate.query("select * from films_genres", (rs, ns) -> rs.getInt("genre_id")).isEmpty());
        assertTrue(jdbcTemplate.query("select * from films", (rs, ns) -> rs.getInt("film_id")).isEmpty());
    }
}