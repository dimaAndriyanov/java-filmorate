package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.CanNotBeFriendWithYourselfException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Repository
public class UsersFriendsDaoImpl implements UsersFriendsDao {
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Autowired
    public UsersFriendsDaoImpl(JdbcTemplate jdbcTemplate,
                               @Qualifier("userDbStorage") UserStorage userStorage,
                               UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
        this.userMapper = userMapper;
    }

    @Override
    public void addFriendById(int id, int friendId) {
        if (id == friendId) {
            throw new CanNotBeFriendWithYourselfException("User can not be friend with himself");
        }
        userStorage.getById(id);
        userStorage.getById(friendId);
        try {
            jdbcTemplate.update("insert into users_friends (user_id, friend_id) values (?, ?)", id, friendId);
        } catch (DuplicateKeyException ignored) {
        }
    }

    @Override
    public void deleteFriendById(int id, int friendId) {
        if (id == friendId) {
            throw new CanNotBeFriendWithYourselfException("User can not be friend with himself");
        }
        userStorage.getById(id);
        userStorage.getById(friendId);
        jdbcTemplate.update("delete from users_friends where user_id = ? and friend_id = ?", id, friendId);
    }

    @Override
    public List<User> getCommonFriendsById(int id, int otherId) {
        userStorage.getById(id);
        userStorage.getById(otherId);
        String sqlFirstUserFriendsQuery = "select friend_id from users_friends where user_id = ?";
        String sqlSecondUserFriendsQuery = "select friend_id from users_friends where user_id = ?";
        String sql = "select * from users where user_id in ("
                + sqlFirstUserFriendsQuery + " intersect " + sqlSecondUserFriendsQuery + ")";
        return jdbcTemplate.query(sql, userMapper, id, otherId);
    }
}