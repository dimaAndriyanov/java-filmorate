package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }

    @Override
    public List<User> getAll() {
        String sql = "select * from users";
        return jdbcTemplate.query(sql, userMapper);
    }

    @Override
    public User getById(int id) {
        String sql = "select * from users where user_id = ?";
        Optional<User> result =
                jdbcTemplate.query(sql, userMapper, id).stream().findAny();
        if (result.isPresent()) {
            return result.get();
        } else {
            throw new ObjectNotFoundException(String.format("User with id %d not found", id));
        }
    }

    @Override
    public User add(User user) {
        if (user == null) {
            throw new NullPointerException("Can not add null user");
        }
        String sql = "select login from users where login = ?";
        if (jdbcTemplate.query(sql, (resultSet, rowNumber) -> resultSet.getString("login"), user.getLogin())
                .stream().findAny().isPresent()) {
            throw new LoginAlreadyInUseException(String.format("Login %s is already in use", user.getLogin()));
        }
        sql = "select email from users where email = ?";
        if (jdbcTemplate.query(sql, (resultSet, rowNumber) -> resultSet.getString("email"), user.getEmail())
                .stream().findAny().isPresent()) {
            throw new EmailAlreadyInUseException(String.format("Email address %s is already in use", user.getEmail()));
        }

        user.fillName();
        user.deleteAllFriends();

        String insertSql = "insert into users (user_name, email, login, birthday) values (?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, new String[]{"user_id"});
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getLogin());
            ps.setString(4, user.getBirthday().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        log.info("New User with id {} has been added to DB", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        if (user == null) {
            throw new NullPointerException("Can not update null user");
        }
        User oldUser = getById(user.getId());

        String sql = "select login from users where login = ? and user_id <> ?";
        if (jdbcTemplate.query(sql, (resultSet, rowNumber) -> resultSet.getString("login"),
                        user.getLogin(), user.getId())
                .stream().findAny().isPresent()) {
            throw new LoginAlreadyInUseException(String.format("Login %s is already in use", user.getLogin()));
        }
        sql = "select email from users where email = ? and user_id <> ?";
        if (jdbcTemplate.query(sql, (resultSet, rowNumber) -> resultSet.getString("email"),
                        user.getEmail(), user.getId())
                .stream().findAny().isPresent()) {
            throw new EmailAlreadyInUseException(String.format("Email address %s is already in use", user.getEmail()));
        }
        if (!user.getFriendsIds().equals(oldUser.getFriendsIds())) {
            throw new NotEqualFriendlistsException("Friendlists of updated and original users must be Equal");
        }

        user.fillName();
        sql = "update users set user_name = ?, email = ?, login = ?, birthday = ? where user_id = ?";
        jdbcTemplate.update(sql, user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(), user.getId());

        log.info("User with id {} has been updated in DB", user.getId());
        return user;
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from users_friends");
        jdbcTemplate.update("delete from films_likes");
        jdbcTemplate.update("delete from users");
        log.info("Table users in DB has been cleared");
    }

    @Override
    public User deleteById(int id) {
        User deletedUser = getById(id);
        jdbcTemplate.update("delete from users_friends where user_id = ? or friend_id = ?", id, id);
        String sqlSubQuery = "(select film_id from films_likes where user_id = ?)";
        jdbcTemplate.update("update films set likes_amount = likes_amount - 1 where film_id in " + sqlSubQuery, id);
        jdbcTemplate.update("delete from films_likes where user_id = ?", id);
        jdbcTemplate.update("delete from users where user_id = ?", id);
        log.info("User with id {} has been removed from DB", id);
        return deletedUser;
    }

    @Override
    public List<User> getFriendsById(int id) {
        getById(id);
        String sqlSubQuery = "(select friend_id from users_friends where user_id = ?)";
        return jdbcTemplate.query("select * from users where user_id in " + sqlSubQuery, userMapper, id);
    }
}