package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaRatingDaoImplTest {
    private final MpaRatingDao mpaRatingDao;

    @Test
    public void testGetAll() {
        List<MpaRating> mpaRatings = mpaRatingDao.getAll();
        assertEquals(5, mpaRatings.size());

        MpaRating testRating = new MpaRating();

        testRating.setId(1);
        testRating.setName("G");
        assertTrue(mpaRatings.contains(testRating));

        testRating.setId(2);
        testRating.setName("PG");
        assertTrue(mpaRatings.contains(testRating));

        testRating.setId(3);
        testRating.setName("PG-13");
        assertTrue(mpaRatings.contains(testRating));

        testRating.setId(4);
        testRating.setName("R");
        assertTrue(mpaRatings.contains(testRating));

        testRating.setId(5);
        testRating.setName("NC-17");
        assertTrue(mpaRatings.contains(testRating));
    }

    @Test
    public void testGetById() {
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> mpaRatingDao.getById(6));
        assertEquals("MPA rating with id 6 not found", exception.getMessage());

        MpaRating pg13Rating = new MpaRating();
        pg13Rating.setId(3);
        pg13Rating.setName("PG-13");

        assertEquals(pg13Rating, mpaRatingDao.getById(3));
    }
}