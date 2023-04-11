package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.HasId;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class StorageTest {
    Storage<HasId> storage;

    void setStorage(Storage<HasId> storage) {
        this.storage = storage;
    }

    public static <T> void assertListEquals(List<T> firstList, List<T> secondList) {
        assertEquals(firstList.size(), secondList.size());
        firstList.forEach(value -> assertTrue(secondList.contains(value)));
        secondList.forEach(value -> assertTrue(firstList.contains(value)));
    }

    List<HasId> addThreeUsers() {
        return List.of(
            storage.add(new User("email@mail.com", "myLogin", LocalDate.of(2000, 10, 20))),
            storage.add(new User("pochta@mail.ru", "otherLogin", LocalDate.of(2010, 5, 15))),
            storage.add(new User("writeMe@mail.org", "bestLogin", LocalDate.of(2005, 1, 23)))
        );
    }

    @Test
    void add() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> storage.add(null));
        assertEquals("Can not add null value", exception.getMessage());

        List<HasId> users = addThreeUsers();
        assertEquals(storage.getAll().size(), 3);
        assertEquals(users.get(0).getId(), 1);
        assertEquals(users.get(1).getId(), 2);
        assertEquals(users.get(2).getId(), 3);
    }

    @Test
    void update() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> storage.update(null));
        assertEquals("Can not update null value", exception.getMessage());

        addThreeUsers();
        HasId notInStorageUser = new User("emailAdress@mail.com", "login", LocalDate.of(2023, 1, 1));
        notInStorageUser.setId(4);
        assertThrows(ObjectNotFoundException.class, () -> storage.update(notInStorageUser));

        HasId newUserWithId1 = new User("newEmail@mail.com", "newLogin", LocalDate.of(1990, 10, 23));
        newUserWithId1.setId(1);
        storage.update(newUserWithId1);
        assertEquals(newUserWithId1, storage.getById(1));
    }

    @Test
    void getAll() {
        List<HasId> listOfAllValues = storage.getAll();
        assertNotNull(listOfAllValues);
        assertTrue(listOfAllValues.isEmpty());

        List<HasId> users = addThreeUsers();
        listOfAllValues = storage.getAll();
        assertListEquals(users, listOfAllValues);
    }

    @Test
    void getById() {
        List<HasId> users = addThreeUsers();
        assertThrows(ObjectNotFoundException.class, () -> storage.getById(4));

        assertEquals(users.get(0), storage.getById(1));
    }

    @Test
    void deleteAll() {
        addThreeUsers();
        assertFalse(storage.getAll().isEmpty());
        storage.deleteAll();
        assertTrue(storage.getAll().isEmpty());
    }

    @Test
    void deleteById() {
        List<HasId> users = addThreeUsers();
        assertThrows(ObjectNotFoundException.class, () -> storage.deleteById(4));

        HasId deletedUser = storage.deleteById(1);
        assertEquals(deletedUser, users.get(0));
        assertEquals(storage.getAll().size(), 2);
        assertThrows(ObjectNotFoundException.class, () -> storage.getById(1));
    }
}