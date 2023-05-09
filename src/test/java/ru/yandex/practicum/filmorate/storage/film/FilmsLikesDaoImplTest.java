package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmsLikesDaoImplTest {
    private final FilmsLikesDao filmsLikesDao;
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @Test
    void addLikeFromUserDeleteLikeFromUser() {
        MpaRating mpa = new MpaRating();
        mpa.setId(1);
        int filmId = filmStorage.add(new Film("name", "desc", LocalDate.of(2000, 1, 1), 1, mpa)).getId();
        int userId = userStorage.add(new User("e@mail.ru", "login", LocalDate.of(1990, 1, 1))).getId();

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> filmsLikesDao.addLikeFromUser(9999, userId));
        assertEquals("Film with id 9999 not found", exception.getMessage());

        exception = assertThrows(ObjectNotFoundException.class, () -> filmsLikesDao.addLikeFromUser(filmId, 9999));
        assertEquals("User with id 9999 not found", exception.getMessage());

        int likesAmount = jdbcTemplate.query("select likes_amount from films where film_id = ?",
                (rs, rn) -> rs.getInt("likes_amount"), filmId).get(0);
        assertEquals(0, likesAmount);
        assertEquals(0, jdbcTemplate.query("select user_id from films_likes where film_id = ?",
                (rs, rn) -> rs.getInt("user_id"), userId).size());

        filmsLikesDao.addLikeFromUser(filmId, userId);
        likesAmount = jdbcTemplate.query("select likes_amount from films where film_id = ?",
                (rs, rn) -> rs.getInt("likes_amount"), filmId).get(0);
        assertEquals(1, likesAmount);
        assertEquals(1, jdbcTemplate.query("select user_id from films_likes where film_id = ?",
                (rs, rn) -> rs.getInt("user_id"), filmId).size());
        assertEquals(userId, jdbcTemplate.query("select user_id from films_likes where film_id = ?",
                (rs, rn) -> rs.getInt("user_id"), filmId).get(0));

        filmsLikesDao.addLikeFromUser(filmId, userId);
        likesAmount = jdbcTemplate.query("select likes_amount from films where film_id = ?",
                (rs, rn) -> rs.getInt("likes_amount"), filmId).get(0);
        assertEquals(1, likesAmount);
        assertEquals(1, jdbcTemplate.query("select user_id from films_likes where film_id = ?",
                (rs, rn) -> rs.getInt("user_id"), filmId).size());
        assertEquals(userId, jdbcTemplate.query("select user_id from films_likes where film_id = ?",
                (rs, rn) -> rs.getInt("user_id"), filmId).get(0));

        exception = assertThrows(ObjectNotFoundException.class, () -> filmsLikesDao.deleteLikeFromUser(9999, userId));
        assertEquals("Film with id 9999 not found", exception.getMessage());

        exception = assertThrows(ObjectNotFoundException.class, () -> filmsLikesDao.deleteLikeFromUser(filmId, 9999));
        assertEquals("User with id 9999 not found", exception.getMessage());

        filmsLikesDao.deleteLikeFromUser(filmId, userId);
        likesAmount = jdbcTemplate.query("select likes_amount from films where film_id = ?",
                (rs, rn) -> rs.getInt("likes_amount"), filmId).get(0);
        assertEquals(0, likesAmount);
        assertEquals(0, jdbcTemplate.query("select user_id from films_likes where film_id = ?",
                (rs, rn) -> rs.getInt("user_id"), filmId).size());

        filmsLikesDao.deleteLikeFromUser(filmId, userId);
        likesAmount = jdbcTemplate.query("select likes_amount from films where film_id = ?",
                (rs, rn) -> rs.getInt("likes_amount"), filmId).get(0);
        assertEquals(0, likesAmount);
        assertEquals(0, jdbcTemplate.query("select user_id from films_likes where film_id = ?",
                (rs, rn) -> rs.getInt("user_id"), filmId).size());
    }
}