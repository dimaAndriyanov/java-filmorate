package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserMapper {
    public List<User> mapUsers(SqlRowSet rowSet) {
        Map<Integer, User> users = new LinkedHashMap<>();
        while (rowSet.next()) {
            if (!users.containsKey(rowSet.getInt("user_id"))) {
                User user = new User (
                        rowSet.getString("email"),
                        rowSet.getString("login"),
                        LocalDate.parse(rowSet.getString("birthday"), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                );
                user.setId(rowSet.getInt("user_id"));
                user.setName(rowSet.getString("user_name"));

                if (rowSet.getInt("friend_id") != 0) {
                    user.addFriendId(rowSet.getInt("friend_id"));
                }

                users.put(user.getId(), user);
            } else {
                User user = users.get(rowSet.getInt("user_id"));

                if (rowSet.getInt("friend_id") != 0) {
                    user.addFriendId(rowSet.getInt("friend_id"));
                }
            }
        }
        return new ArrayList<>(users.values());
    }
}