package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;

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
        user.fillName();
        return super.create(user);
    }

    @Override
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        try {
            user.fillName();
            return super.update(user);
        } catch (ObjectNotFoundException e) {
            log.warn("User update failed due to absence of user with such id");
            throw new ObjectNotFoundException("User with such id not found");
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