package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.EmailAlreadyInUseException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.StorageTest;


import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserStorageTest {
    InMemoryUserStorage userStorage = new InMemoryUserStorage();

    @BeforeEach
    void clearStorage() {
        userStorage.deleteAll();
    }

    @Test
    void getFriendsById() {
        User userWithTwoFriends = new User("email@mail.com", "login", LocalDate.of(2000, 1, 1));
        userWithTwoFriends.addFriendId(2);
        userWithTwoFriends.addFriendId(3);
        userStorage.add(userWithTwoFriends);

        User userWithNoFriends = new User("emailAdderss@mail.ru", "otherLogin", LocalDate.of(1990, 2, 2));
        userStorage.add(userWithNoFriends);

        User userWithFriendNotFromStorage = new User("address@mail.org", "bestLogin", LocalDate.of(2010, 12, 12));
        userWithFriendNotFromStorage.addFriendId(4);
        userStorage.add(userWithFriendNotFromStorage);

        StorageTest.assertListEquals(userStorage.getFriendsById(1),
                List.of(userWithNoFriends, userWithFriendNotFromStorage));

        assertTrue(userStorage.getFriendsById(2).isEmpty());

        assertThrows(ObjectNotFoundException.class, () -> userStorage.getFriendsById(3));
        assertThrows(ObjectNotFoundException.class, () -> userStorage.getFriendsById(4));
    }

    @Test
    void add() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> userStorage.add(null));
        assertEquals("Can not add null user", exception.getMessage());

        userStorage.add(new User("email@mail.com", "login", LocalDate.of(1990, 2, 2)));
        assertThrows(EmailAlreadyInUseException.class, () ->
                userStorage.add(new User("email@mail.com", "otherLogin", LocalDate.of(2010, 5, 5))));
    }

    @Test
    void update() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> userStorage.update(null));
        assertEquals("Can not update null value", exception.getMessage());

        userStorage.add(new User("email@mail.com", "login", LocalDate.of(1990, 2, 2)));
        userStorage.add(new User("otherMail@mail.com", "otherLogin", LocalDate.of(1995, 7, 7)));
        User userWithExistingEmail = new User("email@mail.com", "newLogin", LocalDate.of(1997, 6, 6));
        userWithExistingEmail.setId(2);
        assertThrows(EmailAlreadyInUseException.class, () -> userStorage.update(userWithExistingEmail));
    }
}