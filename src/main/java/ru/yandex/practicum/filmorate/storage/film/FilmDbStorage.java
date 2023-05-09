package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotEqualLikesException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaRatingDao mpaRatingDao;
    private final GenreDao genreDao;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaRatingDao mpaRatingDao, GenreDao genreDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaRatingDao = mpaRatingDao;
        this.genreDao = genreDao;
    }

    @Override
    public List<Film> getAll() {
        return jdbcTemplate.query("select * from films", (resultSet, rowNumber) -> mapFilm(resultSet));
    }

    @Override
    public Film getById(int id) {
        Optional<Film> resultFilm =
                jdbcTemplate.query("select * from films where film_id = ?",
                        (resultSet, rowNumber) -> mapFilm(resultSet), id).stream().findAny();
        if (resultFilm.isPresent()) {
            return resultFilm.get();
        } else {
            throw new ObjectNotFoundException(String.format("Film with id %d not found", id));
        }
    }

    @Override
    public Film add(Film film) {
        if (film == null) {
            throw new NullPointerException("Can not add null film");
        }
        mpaRatingDao.getById(film.getMpa().getId());
        film.getGenres().forEach(genre -> genreDao.getById(genre.getId()));

        film.deleteAllLikes();

        String sql = "insert into films (film_name, description, release_date, duration, likes_amount, mpa_rating_id)" +
                " values (?, ?, ?, ?, ?, ?) ";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setString(3, film.getReleaseDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getLikesFromUsersIds().size());
            ps.setInt(6, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        film.getGenres().forEach(genre ->
                jdbcTemplate.update("insert into films_genres (film_id, genre_id) values (?, ?)",
                        film.getId(), genre.getId()));
        log.info("New film with id {} has been added to DB", film.getId());
        return getById(film.getId());
    }

    @Override
    public Film update(Film film) {
        if (film == null) {
            throw new NullPointerException("Can not update null film");
        }
        mpaRatingDao.getById(film.getMpa().getId());
        film.getGenres().forEach(genre -> genreDao.getById(genre.getId()));

        Film oldFilm = getById(film.getId());
        if (!film.getLikesFromUsersIds().equals(oldFilm.getLikesFromUsersIds())) {
            throw new NotEqualLikesException("Updated and original films must have equal likes");
        }

        String sql = "update films set film_name = ?, description = ?, release_date = ?, " +
                "duration = ?, mpa_rating_id = ? where film_id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        jdbcTemplate.update("delete from films_genres where film_id = ?", film.getId());
        film.getGenres().forEach(genre ->
                jdbcTemplate.update("insert into films_genres (film_id, genre_id) values (?, ?)",
                        film.getId(), genre.getId()));
        log.info("Film with id {} has been updated in DB", film.getId());
        return getById(film.getId());
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from films_likes");
        jdbcTemplate.update("delete from films_genres");
        jdbcTemplate.update("delete from films");
        log.info("Table films in DB has been cleared");
    }

    @Override
    public Film deleteById(int id) {
        Film deletedFilm = getById(id);
        jdbcTemplate.update("delete from films_likes where film_id = ?", id);
        jdbcTemplate.update("delete from films_genres where film_id = ?", id);
        jdbcTemplate.update("delete from films where film_id = ?", id);
        log.info("Film with id {} has been removed from DB", id);
        return deletedFilm;
    }

    @Override
    public List<Film> getPopular(int count) {
        return jdbcTemplate.query("select * from films order by likes_amount desc limit ?",
                (resultSet, rowSet) -> mapFilm(resultSet), count);
    }

    @Override
    public void addToFilmsPopularity(Film film) {
    }

    @Override
    public void deleteFromFilmsPopularity(int id) {
    }

    @Override
    public void deleteAllLikesFromUserById(int id) {
    }

    private Film mapFilm(ResultSet resultSet) throws SQLException {
        Film resultFilm = new Film(
                resultSet.getString("film_name"),
                resultSet.getString("description"),
                LocalDate.parse(resultSet.getString("release_date"), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                resultSet.getInt("duration"),
                mpaRatingDao.getById(resultSet.getInt("mpa_rating_id"))
        );
        resultFilm.setId(resultSet.getInt("film_id"));
        genreDao.getByFilmId(resultFilm.getId()).forEach(resultFilm::addGenre);
        jdbcTemplate.query("select user_id from films_likes where film_id = ?",
                (resSet, rowNumber) -> resSet.getInt("user_id"), resultFilm.getId())
                .forEach(resultFilm::addLikeFromUserId);
        return resultFilm;
    }
}