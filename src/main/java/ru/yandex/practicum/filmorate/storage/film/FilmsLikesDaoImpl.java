package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Repository
public class FilmsLikesDaoImpl implements FilmsLikesDao {
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmsLikesDaoImpl(JdbcTemplate jdbcTemplate,
                             @Qualifier("userDbStorage") UserStorage userStorage,
                             @Qualifier("filmDbStorage") FilmStorage filmstorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
        this.filmStorage = filmstorage;
    }

    @Override
    public void addLikeFromUser(int filmId, int userId) {
        filmStorage.getById(filmId);
        userStorage.getById(userId);
        try {
            jdbcTemplate.update("insert into films_likes (film_id, user_id) values (?, ?)", filmId, userId);
            jdbcTemplate.update("update films set likes_amount = likes_amount + 1 where film_id = ?", filmId);
        }
        catch (DuplicateKeyException ignored) {}
    }

    @Override
    public void deleteLikeFromUser(int filmId, int userId) {
        filmStorage.getById(filmId);
        userStorage.getById(userId);
        if (!jdbcTemplate.query("select * from films_likes where film_id = ? and user_id = ?",
                (resultSet, rowNumber) -> resultSet.getInt("film_id"), filmId, userId)
                .isEmpty()) {
            jdbcTemplate.update("delete from films_likes where film_id = ? and user_id = ?", filmId, userId);
            jdbcTemplate.update("update films set likes_amount = likes_amount - 1 where film_id = ?", filmId);
        }
    }
}