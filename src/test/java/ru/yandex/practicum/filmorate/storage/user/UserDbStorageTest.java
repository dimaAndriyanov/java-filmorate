package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmsLikesDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final UserDbStorage userStorage;
    private final UsersFriendsDbStorage usersFriendsDbStorage;
    private final FilmDbStorage filmStorage;
    private final FilmsLikesDbStorage filmsLikesDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void userDbStorageTest() {
        userStorage.deleteAll();

        assertTrue(userStorage.getAll().isEmpty());

        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> userStorage.getById(9999));
        assertEquals("User with id 9999 not found", objectNotFoundException.getMessage());

        User user011 = new User("e011@mail.ru", "login011", LocalDate.of(1990, 2, 1));
        user011.setName("name011");
        user011.addFriendId(9999);
        user011 = userStorage.add(user011);
        User user012 = new User("e012@mail.ru", "login012", LocalDate.of(1990, 2, 2));
        user012.setName("name012");
        user012 = userStorage.add(user012);
        User user013 = new User("e013@mail.ru", "login013", LocalDate.of(1990, 2, 3));
        user013 = userStorage.add(user013);

        List<User> users = userStorage.getAll();
        assertEquals(3, users.size());
        assertTrue(users.contains(user011));
        assertTrue(users.contains(user012));
        assertTrue(users.contains(user013));

        assertEquals(user012, userStorage.getById(user012.getId()));

        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> userStorage.add(null));
        assertEquals("Can not add null user", nullPointerException.getMessage());

        User userWithExistingLogin = new User("e014@mail.ru", "login011", LocalDate.of(1990, 2, 4));
        LoginAlreadyInUseException loginAlreadyInUseException = assertThrows(LoginAlreadyInUseException.class,
                () -> userStorage.add(userWithExistingLogin));
        assertEquals(String.format("Login %s is already in use", userWithExistingLogin.getLogin()),
                loginAlreadyInUseException.getMessage());

        User userWithExistingEmail = new User("e011@mail.ru", "login015", LocalDate.of(1990, 2, 5));
        EmailAlreadyInUseException emailAlreadyInUseException = assertThrows(EmailAlreadyInUseException.class,
                () -> userStorage.add(userWithExistingEmail));
        assertEquals(String.format("Email address %s is already in use", userWithExistingEmail.getEmail()),
                emailAlreadyInUseException.getMessage());

        assertEquals("login013", userStorage.getById(user013.getId()).getName());
        assertTrue(userStorage.getById(user011.getId()).getFriendsIds().isEmpty());

        nullPointerException = assertThrows(NullPointerException.class, () -> userStorage.update(null));
        assertEquals("Can not update null user", nullPointerException.getMessage());

        User notExistingUser = new User("e016@mail.ru", "login016", LocalDate.of(1990, 2, 6));
        notExistingUser.setId(9999);
        objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> userStorage.update(notExistingUser));
        assertEquals("User with id 9999 not found", objectNotFoundException.getMessage());

        userWithExistingLogin.setId(user013.getId());
        loginAlreadyInUseException = assertThrows(LoginAlreadyInUseException.class,
                () -> userStorage.update(userWithExistingLogin));
        assertEquals(String.format("Login %s is already in use", userWithExistingLogin.getLogin()),
                loginAlreadyInUseException.getMessage());

        userWithExistingEmail.setId(user013.getId());
        emailAlreadyInUseException = assertThrows(EmailAlreadyInUseException.class,
                () -> userStorage.update(userWithExistingEmail));
        assertEquals(String.format("Email address %s is already in use", userWithExistingEmail.getEmail()),
                emailAlreadyInUseException.getMessage());

        User userWithDifferentFriends = new User("e017@mail.ru", "login017", LocalDate.of(1990, 2, 7));
        userWithDifferentFriends.addFriendId(user011.getId());
        userWithDifferentFriends.setId(user013.getId());
        NotEqualFriendlistsException notEqualFriendlistsException = assertThrows(NotEqualFriendlistsException.class,
                () -> userStorage.update(userWithDifferentFriends));
        assertEquals("Friendlists of updated and original users must be Equal",
                notEqualFriendlistsException.getMessage());

        User updatedUser = new User("e018@mail.ru", "login018", LocalDate.of(1990, 2, 8));
        updatedUser.setName("name018");
        updatedUser.setId(user013.getId());
        userStorage.update(updatedUser);
        assertNotEquals(user013, userStorage.getById(user013.getId()));
        assertEquals(updatedUser, userStorage.getById(user013.getId()));

        objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> userStorage.getFriendsById(9999));
        assertEquals("User with id 9999 not found", objectNotFoundException.getMessage());

        usersFriendsDbStorage.addFriendById(user011.getId(), user012.getId());
        usersFriendsDbStorage.addFriendById(user011.getId(), user013.getId());
        usersFriendsDbStorage.addFriendById(user012.getId(), user013.getId());

        assertEquals(2, userStorage.getFriendsById(user011.getId()).size());
        assertTrue(userStorage.getFriendsById(user011.getId()).contains(userStorage.getById(user012.getId())));
        assertTrue(userStorage.getFriendsById(user011.getId()).contains(userStorage.getById(user013.getId())));

        assertEquals(1, userStorage.getFriendsById(user012.getId()).size());
        assertEquals(userStorage.getById(user013.getId()), userStorage.getFriendsById(user012.getId()).get(0));

        assertTrue(userStorage.getFriendsById(user013.getId()).isEmpty());

        objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> userStorage.deleteById(9999));
        assertEquals("User with id 9999 not found", objectNotFoundException.getMessage());

        MpaRating mpa = new MpaRating();
        mpa.setId(1);
        Film film011 = new Film("name011", "desc011", LocalDate.of(2000, 2, 1), 11, mpa);
        film011 = filmStorage.add(film011);
        filmsLikesDbStorage.addLikeFromUser(film011.getId(), user012.getId());

        assertEquals(2, jdbcTemplate.query("select * from users_friends where user_id = ? or friend_id = ?",
                (rs, rn) -> rs.getInt("user_id"), user012.getId(), user012.getId()).size());
        assertEquals(1, jdbcTemplate.query("select * from films_likes where user_id = ?",
                (rs, rn) -> rs.getInt("film_id"), user012.getId()).size());

        assertEquals(1, filmStorage.getById(film011.getId()).getLikesFromUsersIds().size());
        assertTrue(filmStorage.getById(film011.getId()).getLikesFromUsersIds().contains(user012.getId()));

        assertEquals(1, jdbcTemplate.query("select likes_amount from films where film_id = ?",
                (rs, rn) -> rs.getInt("likes_amount"), film011.getId()).get(0));

        user012 = userStorage.deleteById(user012.getId());

        assertEquals(1, userStorage.getFriendsById(user011.getId()).size());
        assertFalse(userStorage.getFriendsById(user011.getId()).contains(user012));

        assertTrue(jdbcTemplate.query("select * from users_friends where user_id = ? or friend_id = ?",
                (rs, rn) -> rs.getInt("user_id"), user012.getId(), user012.getId()).isEmpty());
        assertTrue(jdbcTemplate.query("select * from films_likes where user_id = ?",
                (rs, rn) -> rs.getInt("film_id"), user012.getId()).isEmpty());

        assertTrue(filmStorage.getById(film011.getId()).getLikesFromUsersIds().isEmpty());

        assertEquals(0, jdbcTemplate.query("select likes_amount from films where film_id = ?",
                (rs, rn) -> rs.getInt("likes_amount"), film011.getId()).get(0));

        assertEquals(2, userStorage.getAll().size());
        assertFalse(userStorage.getAll().contains(user012));
        int user012Id = user012.getId();
        assertThrows(ObjectNotFoundException.class, () -> userStorage.getById(user012Id));

        userStorage.deleteAll();
        assertTrue(userStorage.getAll().isEmpty());
        assertTrue(jdbcTemplate.query("select * from users_friends", (rs, rn) -> rs.getInt("user_id")).isEmpty());
        assertTrue(jdbcTemplate.query("select * from films_likes", (rs, rn) -> rs.getInt("film_id")).isEmpty());
        assertTrue(jdbcTemplate.query("select * from users", (rs, rn) -> rs.getInt("user_id")).isEmpty());
    }
}