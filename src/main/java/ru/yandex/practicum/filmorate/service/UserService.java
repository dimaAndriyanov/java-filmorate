package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.CanNotBeFriendWithYourselfException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserStorageException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
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
        if (userId == friendId) {
            throw new CanNotBeFriendWithYourselfException("User can not be friend with himself");
        }
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);
        user.addFriendId(friendId);
        friend.addFriendId(userId);
    }

    public void deleteFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new CanNotBeFriendWithYourselfException("User can not be friend with himself");
        }
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);
        user.deleteFriendId(friendId);
        friend.deleteFriendId(userId);
    }

    public List<User> getFriendsById(int id) {
        return userStorage.getFriendsById(id);
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        User user = userStorage.getById(userId);
        User otherUser = userStorage.getById(otherUserId);
        try {
            return user.getFriendsIds().stream()
                    .filter(id -> otherUser.getFriendsIds().contains(id))
                    .map(userStorage::getById)
                    .collect(Collectors.toList());
        } catch (ObjectNotFoundException exception) {
            throw new UserStorageException(
                    String.format("Friendlist of user with id %d contains user which is not in storage", userId));
        }
    }
}