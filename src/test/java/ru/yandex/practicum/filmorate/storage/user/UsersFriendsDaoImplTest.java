package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.CanNotBeFriendWithYourselfException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UsersFriendsDaoImplTest {
    private final UsersFriendsDao usersFriendsDao;
    private final UserDbStorage userStorage;

    @Test
    public void usersFriendsDaoTest() {
        int user001Id = userStorage.add(new User("e001@mail.ru", "login001", LocalDate.of(1990, 1, 1))).getId();
        int user002Id = userStorage.add(new User("e002@mail.ru", "login002", LocalDate.of(1990, 1, 2))).getId();
        int user003Id = userStorage.add(new User("e003@mail.ru", "login003", LocalDate.of(1990, 1, 3))).getId();

        CanNotBeFriendWithYourselfException canNotBeFriendWithYourselfException =
                assertThrows(CanNotBeFriendWithYourselfException.class,
                        () -> usersFriendsDao.addFriendById(user001Id, user001Id));
        assertEquals("User can not be friend with himself", canNotBeFriendWithYourselfException.getMessage());

        ObjectNotFoundException objectNotFoundException =
                assertThrows(ObjectNotFoundException.class, () -> usersFriendsDao.addFriendById(9999, user001Id));
        assertEquals("User with id 9999 not found", objectNotFoundException.getMessage());
        objectNotFoundException =
                assertThrows(ObjectNotFoundException.class, () -> usersFriendsDao.addFriendById(user001Id, 9999));
        assertEquals("User with id 9999 not found", objectNotFoundException.getMessage());

        assertTrue(userStorage.getFriendsById(user001Id).isEmpty());
        assertTrue(userStorage.getFriendsById(user002Id).isEmpty());
        assertTrue(userStorage.getFriendsById(user003Id).isEmpty());

        usersFriendsDao.addFriendById(user001Id, user002Id);
        assertEquals(1, userStorage.getFriendsById(user001Id).size());
        assertEquals(user002Id, userStorage.getFriendsById(user001Id).get(0).getId());
        assertTrue(userStorage.getFriendsById(user002Id).isEmpty());

        usersFriendsDao.addFriendById(user001Id, user002Id);
        assertEquals(1, userStorage.getFriendsById(user001Id).size());
        assertEquals(user002Id, userStorage.getFriendsById(user001Id).get(0).getId());
        assertTrue(userStorage.getFriendsById(user002Id).isEmpty());

        usersFriendsDao.addFriendById(user002Id, user001Id);
        assertEquals(1, userStorage.getFriendsById(user001Id).size());
        assertEquals(user002Id, userStorage.getFriendsById(user001Id).get(0).getId());
        assertEquals(1, userStorage.getFriendsById(user002Id).size());
        assertEquals(user001Id, userStorage.getFriendsById(user002Id).get(0).getId());

        usersFriendsDao.addFriendById(user002Id, user001Id);
        assertEquals(1, userStorage.getFriendsById(user001Id).size());
        assertEquals(user002Id, userStorage.getFriendsById(user001Id).get(0).getId());
        assertEquals(1, userStorage.getFriendsById(user002Id).size());
        assertEquals(user001Id, userStorage.getFriendsById(user002Id).get(0).getId());

        canNotBeFriendWithYourselfException =
                assertThrows(CanNotBeFriendWithYourselfException.class,
                        () -> usersFriendsDao.deleteFriendById(user001Id, user001Id));
        assertEquals("User can not be friend with himself", canNotBeFriendWithYourselfException.getMessage());

        objectNotFoundException =
                assertThrows(ObjectNotFoundException.class, () -> usersFriendsDao.deleteFriendById(9999, user001Id));
        assertEquals("User with id 9999 not found", objectNotFoundException.getMessage());
        objectNotFoundException =
                assertThrows(ObjectNotFoundException.class, () -> usersFriendsDao.deleteFriendById(user001Id, 9999));
        assertEquals("User with id 9999 not found", objectNotFoundException.getMessage());

        usersFriendsDao.deleteFriendById(user001Id, user002Id);
        assertTrue(userStorage.getFriendsById(user001Id).isEmpty());
        assertEquals(1, userStorage.getFriendsById(user002Id).size());
        assertEquals(user001Id, userStorage.getFriendsById(user002Id).get(0).getId());

        usersFriendsDao.deleteFriendById(user001Id, user002Id);
        assertTrue(userStorage.getFriendsById(user001Id).isEmpty());
        assertEquals(1, userStorage.getFriendsById(user002Id).size());
        assertEquals(user001Id, userStorage.getFriendsById(user002Id).get(0).getId());

        usersFriendsDao.deleteFriendById(user002Id, user001Id);
        assertTrue(userStorage.getFriendsById(user001Id).isEmpty());
        assertTrue(userStorage.getFriendsById(user002Id).isEmpty());

        usersFriendsDao.deleteFriendById(user002Id, user001Id);
        assertTrue(userStorage.getFriendsById(user001Id).isEmpty());
        assertTrue(userStorage.getFriendsById(user002Id).isEmpty());

        objectNotFoundException =
                assertThrows(ObjectNotFoundException.class, () ->
                        usersFriendsDao.getCommonFriendsById(9999, user001Id));
        assertEquals("User with id 9999 not found", objectNotFoundException.getMessage());
        objectNotFoundException =
                assertThrows(ObjectNotFoundException.class,
                        () -> usersFriendsDao.getCommonFriendsById(user001Id, 9999));
        assertEquals("User with id 9999 not found", objectNotFoundException.getMessage());

        assertTrue(usersFriendsDao.getCommonFriendsById(user001Id, user002Id).isEmpty());

        usersFriendsDao.addFriendById(user001Id, user002Id);
        assertTrue(usersFriendsDao.getCommonFriendsById(user001Id, user002Id).isEmpty());

        usersFriendsDao.addFriendById(user001Id, user003Id);
        usersFriendsDao.addFriendById(user002Id, user003Id);
        usersFriendsDao.addFriendById(user003Id, user001Id);

        assertEquals(1, usersFriendsDao.getCommonFriendsById(user001Id, user002Id).size());
        assertEquals(user003Id, usersFriendsDao.getCommonFriendsById(user001Id, user002Id).get(0).getId());

        assertTrue(usersFriendsDao.getCommonFriendsById(user002Id, user003Id).isEmpty());

        assertEquals(1, usersFriendsDao.getCommonFriendsById(user003Id, user003Id).size());
        assertEquals(user001Id, usersFriendsDao.getCommonFriendsById(user003Id, user003Id).get(0).getId());
    }
}