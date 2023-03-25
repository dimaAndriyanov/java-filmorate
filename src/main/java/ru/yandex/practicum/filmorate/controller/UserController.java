package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController extends Controller<User> {

    @Override
    @GetMapping
    public List<User> getAll() {
        return super.getAll();
    }

    @Override
    @PostMapping
    public User create(@RequestBody User user) {
        return super.create(user);
    }

    @Override
    @PutMapping
    public User update(@RequestBody User user) {
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
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Validation failed due to incorrect User email address");
            throw new ValidationException("User email address must not be empty");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("Validation failed due to incorrect User email address");
            throw new ValidationException("User email address is incorrect");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Validation failed due to incorrect User login");
            throw new ValidationException("User login must not be empty");
        }
        if (user.getLogin().contains(" ")) {
            log.warn("Validation failed due to incorrect User login");
            throw new ValidationException("User login must not contain spaces");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null) {
            log.warn("Validation failed due to incorrect User birthday");
            throw new ValidationException("User birthday must not be empty");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Validation failed due to incorrect User birthday");
            throw new ValidationException("User birthday must not be in future");
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