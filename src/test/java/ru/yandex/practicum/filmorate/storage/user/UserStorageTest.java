package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

abstract class UserStorageTest {
    UserStorage userStorage;

    void setUserStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    List<User> addThreeUsers() {
        return List.of(
                userStorage.add(new User("email@mail.com", "myLogin", LocalDate.of(2000, 1, 1))),
                userStorage.add(new User("pochta@mail.ru", "otherLogin", LocalDate.of(2000, 2, 2))),
                userStorage.add(new User("writeMe@mail.org", "bestLogin", LocalDate.of(2000, 3, 3)))
        );
    }

    @Test
    void getAll() {
        List<User> listOfAllUsers = userStorage.getAll();
        assertNotNull(listOfAllUsers);
        assertTrue(listOfAllUsers.isEmpty());

        List<User> users = addThreeUsers();
        listOfAllUsers = userStorage.getAll();
        assertEquals(new HashSet<>(users), new HashSet<>(listOfAllUsers));
    }

    @Test
    void getById() {
        List<User> users = addThreeUsers();
        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> userStorage.getById(4));
        assertEquals("User with id 4 not found", objectNotFoundException.getMessage());
        assertEquals(users.get(0), userStorage.getById(1));
    }

    @Test
    void add() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> userStorage.add(null));
        assertEquals("Can not add null user", nullPointerException.getMessage());

        List<User> users = addThreeUsers();

        User userWithLoginAlreadyInUse = new User("test@mail.com", "myLogin", LocalDate.of(2000, 4, 4));
        LoginAlreadyInUseException loginAlreadyInUseException = assertThrows(LoginAlreadyInUseException.class,
                () -> userStorage.add(userWithLoginAlreadyInUse));
        assertEquals("Login myLogin is already in use", loginAlreadyInUseException.getMessage());

        User userWithEmailAlreadyInUse = new User("email@mail.com", "testLogin", LocalDate.of(2000, 5, 5));
        EmailAlreadyInUseException emailAlreadyInUseException = assertThrows(EmailAlreadyInUseException.class,
                () -> userStorage.add(userWithEmailAlreadyInUse));
        assertEquals("Email address email@mail.com is already in use", emailAlreadyInUseException.getMessage());

        assertEquals(3, userStorage.getAll().size());
        assertEquals(1, users.get(0).getId());
        assertEquals(2, users.get(1).getId());
        assertEquals(3, users.get(2).getId());

        assertEquals("myLogin", users.get(0).getName());
    }

    @Test
    void update() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> userStorage.update(null));
        assertEquals("Can not update null user", nullPointerException.getMessage());

        addThreeUsers();

        User notFromStorageUser = new User("test@mail.com", "testLogin", LocalDate.of(2000, 4, 4));
        notFromStorageUser.setId(4);
        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> userStorage.update(notFromStorageUser));
        assertEquals("User with id 4 not found", objectNotFoundException.getMessage());

        User userWithLoginAlreadyInUse = new User("test@mail.ru", "myLogin", LocalDate.of(2000, 5, 5));
        userWithLoginAlreadyInUse.setId(2);
        LoginAlreadyInUseException loginAlreadyInUseException = assertThrows(LoginAlreadyInUseException.class,
                () -> userStorage.update(userWithLoginAlreadyInUse));
        assertEquals("Login myLogin is already in use", loginAlreadyInUseException.getMessage());

        User userWithEmailAlreadyInUse = new User("email@mail.com", "newTestLogin", LocalDate.of(2000, 6, 6));
        userWithEmailAlreadyInUse.setId(2);
        EmailAlreadyInUseException emailAlreadyInUseException = assertThrows(EmailAlreadyInUseException.class,
                () -> userStorage.update(userWithEmailAlreadyInUse));
        assertEquals("Email address email@mail.com is already in use", emailAlreadyInUseException.getMessage());

        userStorage.getById(1).addFriendId(2);
        userStorage.getById(2).addFriendId(1);

        User userWithDifferentFriendlist = new User("newEmail@mail.com", "newLogin", LocalDate.of(2000, 7, 7));
        userWithDifferentFriendlist.setId(1);
        NotEqualFriendlistsException notEqualFriendlistsException = assertThrows(NotEqualFriendlistsException.class,
                () -> userStorage.update(userWithDifferentFriendlist));
        assertEquals("Friendlists of updated and original users must be Equal",
                notEqualFriendlistsException.getMessage());

        User originalUserWithIdOne = userStorage.getById(1);
        User updatedUserWithIdOne = new User("updatedEmail@mail.com", "updatedLogin", LocalDate.of(2000, 8, 8));
        updatedUserWithIdOne.setId(1);
        updatedUserWithIdOne.addFriendId(2);
        userStorage.update(updatedUserWithIdOne);
        assertEquals(updatedUserWithIdOne, userStorage.getById(1));
        assertFalse(userStorage.getAll().contains(originalUserWithIdOne));
    }

    @Test
    void deleteAll() {
        addThreeUsers();
        assertFalse(userStorage.getAll().isEmpty());
        userStorage.deleteAll();
        assertTrue(userStorage.getAll().isEmpty());
    }

    @Test
    void deleteById() {
        addThreeUsers();
        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> userStorage.deleteById(4));
        assertEquals("User with id 4 not found", objectNotFoundException.getMessage());

        userStorage.getById(1).addFriendId(2);
        userStorage.getById(2).addFriendId(1);
        User deletedUser = userStorage.deleteById(1);
        assertEquals(2, userStorage.getAll().size());
        assertFalse(userStorage.getAll().contains(deletedUser));
        assertThrows(ObjectNotFoundException.class, () -> userStorage.getById(1));
        assertTrue(userStorage.getById(2).getFriendsIds().isEmpty());
    }

    @Test
    void getFriendsById() {
        addThreeUsers();
        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> userStorage.getFriendsById(4));
        assertEquals("User with id 4 not found", objectNotFoundException.getMessage());

        assertTrue(userStorage.getFriendsById(1).isEmpty());

        userStorage.getById(1).addFriendId(2);
        userStorage.getById(2).addFriendId(1);

        userStorage.getById(1).addFriendId(3);
        userStorage.getById(3).addFriendId(1);

        userStorage.getById(3).addFriendId(4);

        UserStorageException userStorageException = assertThrows(UserStorageException.class,
                () -> userStorage.getFriendsById(3));
        assertEquals("Friendlist of user with id 3 contains user which is not in storage",
                userStorageException.getMessage());

        assertFalse(userStorage.getFriendsById(1).isEmpty());

        assertEquals(Set.of(userStorage.getById(2), userStorage.getById(3)),
                new HashSet<>(userStorage.getFriendsById(1)));
    }
}