package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EmailAlreadyInUseException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage extends InMemoryStorage<User> implements UserStorage {
    @Override
    public List<User> getFriendsById(int id) {
        return getById(id).getFriendsIds().stream().map(this::getById).collect(Collectors.toList());
    }

    @Override
    public User getById(int id) {
        try {
            return super.getById(id);
        } catch (ObjectNotFoundException exception) {
            throw new ObjectNotFoundException(String.format("User with %d id not found", id));
        }
    }

    @Override
    public User add(User user) {
        if (user == null) {
            throw new NullPointerException("Can not add null user");
        }
        if (getAll().stream().anyMatch(value -> value.getEmail().equals(user.getEmail()))) {
            throw new EmailAlreadyInUseException(String.format("Email address %s is already in use", user.getEmail()));
        }
        user.fillName();
        User result = super.add(user);
        log.info("New User with id {} has been added", result.getId());
        return result;
    }

    @Override
    public User update(User user) {
        if (user == null) {
            throw new NullPointerException("Can not update null value");
        }
        try {
            getById(user.getId());
        } catch (ObjectNotFoundException exception) {
            throw new ObjectNotFoundException(String.format("User with %d id not found", user.getId()));
        }
        if (getAll().stream().anyMatch(value ->
                        value.getEmail().equals(user.getEmail()) && value.getId() != user.getId())) {
            throw new EmailAlreadyInUseException(String.format("Email address %s is already in use", user.getEmail()));
        }
        user.fillName();
        User result = super.update(user);
        log.info("User with id {} has been updated", result.getId());
        return result;
    }

    @Override
    public void deleteAll() {
        super.deleteAll();
        log.info("UserStorage has been cleared");
    }

    @Override
    public User deleteById(int id) {
        try {
            User result = super.deleteById(id);
            log.info("User with id {} has been removed", result.getId());
            return result;
        } catch (ObjectNotFoundException exception) {
            throw new ObjectNotFoundException(String.format("User with %d id not found", id));
        }
    }
}