package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private int nextId = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(int id) {
        User result = users.get(id);
        if (result != null) {
            return result;
        } else {
            throw new ObjectNotFoundException(String.format("User with id %d not found", id));
        }
    }

    @Override
    public User add(User user) {
        if (user == null) {
            throw new NullPointerException("Can not add null user");
        }
        if (users.values().stream().anyMatch(value -> value.getLogin().equals(user.getLogin()))) {
            throw new LoginAlreadyInUseException(String.format("Login %s is already in use", user.getLogin()));
        }
        if (users.values().stream().anyMatch(value -> value.getEmail().equals(user.getEmail()))) {
            throw new EmailAlreadyInUseException(String.format("Email address %s is already in use", user.getEmail()));
        }
        user.fillName();
        user.setId(getNextId());
        user.deleteAllFriends();
        users.put(user.getId(), user);
        log.info("New User with id {} has been added", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        if (user == null) {
            throw new NullPointerException("Can not update null user");
        }
        if (!users.containsKey(user.getId())) {
            throw new ObjectNotFoundException(String.format("User with id %d not found", user.getId()));
        }
        if (users.values().stream()
                .filter(value -> value.getId() != user.getId())
                .anyMatch(value -> value.getLogin().equals(user.getLogin()))) {
            throw new LoginAlreadyInUseException(String.format("Login %s is already in use", user.getLogin()));
        }
        if (users.values().stream()
                .filter(value -> value.getId() != user.getId())
                .anyMatch(value -> value.getEmail().equals(user.getEmail()))) {
            throw new EmailAlreadyInUseException(String.format("Email address %s is already in use", user.getEmail()));
        }
        if (!user.getFriendsIds().equals(users.get(user.getId()).getFriendsIds())) {
            throw new NotEqualFriendlistsException("Friendlists of updated and original users must be Equal");
        }
        user.fillName();
        users.put(user.getId(), user);
        log.info("User with id {} has been updated", user.getId());
        return user;
    }

    @Override
    public void deleteAll() {
        users.clear();
        log.info("UserStorage has been cleared");
    }

    @Override
    public User deleteById(int id) {
        User user = users.remove(id);
        if (user != null) {
            deleteFriendFromAllUsersById(id);
            log.info("User with id {} has been removed", user.getId());
            return user;
        } else {
            throw new ObjectNotFoundException(String.format("User with id %d not found", id));
        }
    }

    @Override
    public List<User> getFriendsById(int id) {
        User user = getById(id);
        try {
            return user.getFriendsIds().stream().map(this::getById).collect(Collectors.toList());
        } catch (ObjectNotFoundException exception) {
            throw new UserStorageException(
                    String.format("Friendlist of user with id %d contains user which is not in storage", id));
        }
    }

    private int getNextId() {
        return nextId++;
    }

    private void deleteFriendFromAllUsersById(int id) {
        users.values().forEach(value -> value.deleteFriendId(id));
    }
}