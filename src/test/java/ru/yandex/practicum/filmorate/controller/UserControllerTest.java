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

        User nullEmailUser = new User(null, "login", acceptableBirthday);
        nullEmailUser.setName("name");
        e = assertThrows(ValidationException.class, () -> controller.validate(nullEmailUser));
        assertEquals("User email address must not be empty", e.getMessage());

        User emptyEmailUser = new User("", "login", acceptableBirthday);
        emptyEmailUser.setName("name");
        e = assertThrows(ValidationException.class, () -> controller.validate(emptyEmailUser));
        assertEquals("User email address must not be empty", e.getMessage());

        User incorrectEmailUser = new User("address.net", "login", acceptableBirthday);
        incorrectEmailUser.setName("name");
        e = assertThrows(ValidationException.class, () -> controller.validate(incorrectEmailUser));
        assertEquals("User email address is incorrect", e.getMessage());

        User nullLoginUser = new User("address@mail.com", null, acceptableBirthday);
        nullLoginUser.setName("name");
        e = assertThrows(ValidationException.class, () -> controller.validate(nullLoginUser));
        assertEquals("User login must not be empty", e.getMessage());

        User emptyLoginUser = new User("address@mail.com", "", acceptableBirthday);
        emptyLoginUser.setName("name");
        e = assertThrows(ValidationException.class, () -> controller.validate(emptyLoginUser));
        assertEquals("User login must not be empty", e.getMessage());

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

        User nullBirthdayUser = new User("address@mail.com", "login", null);
        nullBirthdayUser.setName("name");
        e = assertThrows(ValidationException.class, () -> controller.validate(nullBirthdayUser));
        assertEquals("User birthday must not be empty", e.getMessage());

        LocalDate futureDate = LocalDate.of(3000, 1, 1);
        User birthdayInFutureUser = new User("address@mail.com", "login", futureDate);
        birthdayInFutureUser.setName("name");
        e = assertThrows(ValidationException.class, () -> controller.validate(birthdayInFutureUser));
        assertEquals("User birthday must not be in future", e.getMessage());

        User acceptableUser = new User("address@mail.com", "login", acceptableBirthday);
        acceptableUser.setName("name");
        assertDoesNotThrow(() -> controller.validate(acceptableUser));
    }
}