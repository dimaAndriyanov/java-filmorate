package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    @Test
    public void validateTest() {
        UserController controller = new UserController();
        LocalDate acceptableBirthday = LocalDate.of(2000, 1, 1);
        ValidationException e;

        e = assertThrows(ValidationException.class, () -> controller.validate(null));
        assertEquals("User must not be empty", e.getMessage());

        User incorrectLoginUser = new User("address@mail.com", "incorrect login", acceptableBirthday);
        incorrectLoginUser.setName("name");
        e = assertThrows(ValidationException.class, () -> controller.validate(incorrectLoginUser));
        assertEquals("User login must not contain spaces", e.getMessage());

        User nullNameUser = new User("address@mail.com", "login", acceptableBirthday);
        nullNameUser.setName(null);
        assertDoesNotThrow(() -> controller.validate(nullNameUser));
        assertEquals(nullNameUser.getName(), nullNameUser.getLogin());

        User emptyNameUser = new User("address@mail.com", "login", acceptableBirthday);
        emptyNameUser.setName("");
        assertDoesNotThrow(() -> controller.validate(emptyNameUser));
        assertEquals(emptyNameUser.getName(), emptyNameUser.getLogin());

        User acceptableUser = new User("address@mail.com", "login", acceptableBirthday);
        acceptableUser.setName("name");
        assertDoesNotThrow(() -> controller.validate(acceptableUser));
    }
}