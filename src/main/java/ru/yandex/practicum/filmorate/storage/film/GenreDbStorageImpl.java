package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.DbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorageImpl extends DbStorage implements GenreDbStorage {
    @Autowired
    public GenreDbStorageImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public List<Genre> getAll() {
        return jdbcTemplate.query("select genre_id, genre_name from genres",
                (resultSet, rowNumber) -> mapGenre(resultSet));
    }

    @Override
    public Genre getById(int id) {
        Optional<Genre> resultGenre = jdbcTemplate.query("select genre_id, genre_name from genres where genre_id = ?",
                (resultSet, rowNumber) -> mapGenre(resultSet), id)
                .stream().findAny();
        if (resultGenre.isPresent()) {
            return resultGenre.get();
        } else {
            throw new ObjectNotFoundException(String.format("Genre with id %d not found", id));
        }
    }

    @Override
    public List<Genre> getByFilmId(int filmId) {
        String sqlSubQuery = "(select genre_id from films_genres where film_id = ?)";
        return jdbcTemplate.query("select genre_id, genre_name from genres where genre_id in " + sqlSubQuery,
                (resultSet, rowNumber) -> mapGenre(resultSet), filmId);
    }

    private Genre mapGenre(ResultSet resultSet) throws SQLException {
        Genre resultGenre = new Genre();
        resultGenre.setName(resultSet.getString("genre_name"));
        resultGenre.setId(resultSet.getInt("genre_id"));
        return resultGenre;
    }
}