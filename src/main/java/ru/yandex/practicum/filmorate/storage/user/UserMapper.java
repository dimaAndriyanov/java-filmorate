package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Repository
public class UserMapper implements RowMapper<User> {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        User user = new User(
                resultSet.getString("email"),
                resultSet.getString("login"),
                LocalDate.parse(resultSet.getString("birthday"), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );
        user.setId(resultSet.getInt("user_id"));
        user.setName(resultSet.getString("user_name"));

        String sql = "select friend_id from users_friends where user_id = ?";
        jdbcTemplate.query(sql, (resSet, rowNum) -> resSet.getInt("friend_id"), user.getId())
                .forEach(user::addFriendId);
        return user;
    }
}