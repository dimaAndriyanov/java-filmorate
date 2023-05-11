package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UsersFriendsDbStorage {
    void addFriendById(int id, int friendId);

    void deleteFriendById(int id, int friendId);

    List<User> getCommonFriendsById(int id, int otherId);
}