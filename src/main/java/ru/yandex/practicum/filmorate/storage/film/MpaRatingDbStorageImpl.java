package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.DbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class MpaRatingDbStorageImpl extends DbStorage implements MpaRatingDbStorage {
    @Autowired
    public MpaRatingDbStorageImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public List<MpaRating> getAll() {
        String sql = "select mpa_rating_id, mpa_rating_name from mpa_ratings";
        return jdbcTemplate.query(sql, (resultSet, rowNumber) -> mapMpaRating(resultSet));
    }

    @Override
    public MpaRating getById(int id) {
        String sql = "select mpa_rating_id, mpa_rating_name from mpa_ratings where mpa_rating_id = ?";
        Optional<MpaRating> resultMpaRating =
                jdbcTemplate.query(sql, (resultSet, rowNumber) -> mapMpaRating(resultSet), id)
                        .stream().findAny();
        if (resultMpaRating.isPresent()) {
            return resultMpaRating.get();
        } else {
            throw new ObjectNotFoundException(String.format("MPA rating with id %d not found", id));
        }
    }

    private MpaRating mapMpaRating(ResultSet resultSet) throws SQLException {
        MpaRating mpaRating = new MpaRating();
        mpaRating.setName(resultSet.getString("mpa_rating_name"));
        mpaRating.setId(resultSet.getInt("mpa_rating_id"));
        return mpaRating;
    }
}