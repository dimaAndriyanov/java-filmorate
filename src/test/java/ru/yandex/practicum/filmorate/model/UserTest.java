package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void fillName() {
        User userWithNullName = new User("email@mail.com", "login", LocalDate.of(2000, 1, 1));
        userWithNullName.setName(null);
        userWithNullName.fillName();
        assertEquals("login", userWithNullName.getName());

        User userWithEmptyName = new User("emailAdress@mail.ru", "myLogin", LocalDate.of(1990, 11, 11));
        userWithEmptyName.setName(" ");
        userWithEmptyName.fillName();
        assertEquals("myLogin", userWithEmptyName.getName());

        User userWithProperName = new User("adress@mail.org", "bestLogin", LocalDate.of(1995, 2, 2));
        userWithProperName.setName("ProperName");
        userWithProperName.fillName();
        assertEquals("ProperName", userWithProperName.getName());
    }
}