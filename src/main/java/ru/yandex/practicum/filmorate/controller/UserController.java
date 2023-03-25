package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("users")
@Slf4j
public class UserController extends Controller<User> {

    @Override
    @GetMapping
    public List<User> getAll() {
        return super.getAll();
    }

    @Override
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return super.create(user);
    }

    @Override
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        try {
            return super.update(user);
        } catch (ObjectNotFoundException e) {
            log.warn("User update failed due to absence of user with such id");
            throw new ObjectNotFoundException("User with such id not found");
        }
    }

    @Override
    void validate(User user) {
        if (user == null) {
            log.warn("Validation failed due to empty user");
            throw new ValidationException("User must not be empty");
        }
        if (user.getLogin().contains(" ")) {
            log.warn("Validation failed due to incorrect User login");
            throw new ValidationException("User login must not contain spaces");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    @Override
    void logCreationInfo(User user){
        log.info("User {} has been added to catalogue", user);
    }

    @Override
    void logUpdateInfo(User user) {
        log.info("User {} has been updated", user);
    }
}