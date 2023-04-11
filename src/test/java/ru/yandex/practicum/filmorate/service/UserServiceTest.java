package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.StorageTest;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    UserService service;

    @BeforeEach
    void setService() {
        service = new UserService(new InMemoryUserStorage());
        service.add(new User("emailA@mail.com", "loginA", LocalDate.of(2000, 1, 1)));
        service.add(new User("emailB@mail.com", "loginB", LocalDate.of(2000, 2, 2)));
        service.add(new User("emailC@mail.com", "loginC", LocalDate.of(2000, 3, 3)));
        service.add(new User("emailD@mail.com", "loginD", LocalDate.of(2000, 4, 4)));
        service.add(new User("emailE@mail.com", "loginE", LocalDate.of(2000, 5, 5)));
    }

    @Test
    void addFriend() {
        assertThrows(ObjectNotFoundException.class, () -> service.addFriend(1, 6));
        assertThrows(ObjectNotFoundException.class, () -> service.addFriend(6, 1));

        service.addFriend(1, 2);
        assertEquals(1, service.getById(1).getFriendsIds().size());
        assertTrue(service.getById(1).getFriendsIds().contains(2));
        assertEquals(1, service.getById(2).getFriendsIds().size());
        assertTrue(service.getById(2).getFriendsIds().contains(1));

        service.addFriend(1, 3);
        assertEquals(2, service.getById(1).getFriendsIds().size());
        assertTrue(service.getById(1).getFriendsIds().contains(2));
        assertTrue(service.getById(1).getFriendsIds().contains(3));
        assertEquals(1, service.getById(2).getFriendsIds().size());
        assertTrue(service.getById(2).getFriendsIds().contains(1));
        assertEquals(1, service.getById(3).getFriendsIds().size());
        assertTrue(service.getById(3).getFriendsIds().contains(1));

        assertTrue(service.getById(4).getFriendsIds().isEmpty());
        assertTrue(service.getById(5).getFriendsIds().isEmpty());
    }

    @Test
    void deleteFriend() {
        service.addFriend(1, 2);
        service.addFriend(1, 3);
        service.deleteFriend(1, 2);

        assertEquals(1, service.getById(1).getFriendsIds().size());
        assertTrue(service.getById(1).getFriendsIds().contains(3));
        assertFalse(service.getById(1).getFriendsIds().contains(2));
        assertTrue(service.getById(2).getFriendsIds().isEmpty());
        assertEquals(1, service.getById(3).getFriendsIds().size());
        assertTrue(service.getById(3).getFriendsIds().contains(1));
    }

    @Test
    void getCommonFriends() {
        service.addFriend(1, 2);
        service.addFriend(1, 3);
        service.addFriend(1, 4);

        service.addFriend(3, 5);
        service.addFriend(4, 5);

        StorageTest.assertListEquals(service.getCommonFriends(1, 5), List.of(service.getById(3), service.getById(4)));
        StorageTest.assertListEquals(service.getCommonFriends(2, 3), List.of(service.getById(1)));
        assertTrue(service.getCommonFriends(2, 5).isEmpty());
    }
}