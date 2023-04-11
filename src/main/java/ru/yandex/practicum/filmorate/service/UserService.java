package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.CanNotBeFriendWithYourselfException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService extends StorageService<User> {

    @Autowired
    public UserService(UserStorage userStorage) {
        super(userStorage);
    }

    public void addFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new CanNotBeFriendWithYourselfException("User can not be friend with himself");
        }
        User user = storage.getById(userId);
        User friend = storage.getById(friendId);
        user.addFriendId(friendId);
        friend.addFriendId(userId);
    }

    public void deleteFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new CanNotBeFriendWithYourselfException("User can not be friend with himself");
        }
        User user = storage.getById(userId);
        User friend = storage.getById(friendId);
        user.deleteFriendId(friendId);
        friend.deleteFriendId(userId);
    }

    public List<User> getFriendsById(int id) {
        return ((UserStorage) storage).getFriendsById(id);
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        User user = storage.getById(userId);
        User otherUser = storage.getById(otherUserId);
        return user.getFriendsIds().stream()
                .filter(id -> otherUser.getFriendsIds().contains(id))
                .map(storage::getById)
                .collect(Collectors.toList());
    }
}