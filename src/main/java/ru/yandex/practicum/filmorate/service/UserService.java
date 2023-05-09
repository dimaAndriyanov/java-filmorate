package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.user.UsersFriendsDao;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final UsersFriendsDao usersFriendsDao;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, UsersFriendsDao usersFriendsDao) {
        this.userStorage = userStorage;
        this.usersFriendsDao = usersFriendsDao;
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(int id) {
        return userStorage.getById(id);
    }

    public User add(User user) {
        return userStorage.add(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public void addFriend(int userId, int friendId) {
        usersFriendsDao.addFriendById(userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        usersFriendsDao.deleteFriendById(userId, friendId);
    }

    public List<User> getFriendsById(int id) {
        return userStorage.getFriendsById(id);
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        return usersFriendsDao.getCommonFriendsById(userId, otherUserId);
    }
}