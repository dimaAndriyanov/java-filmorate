package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotEqualLikesException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.DbStorage;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class FilmDbStorage extends DbStorage implements FilmStorage {
    private final MpaRatingDbStorage mpaRatingDbStorage;
    private final GenreDbStorage genreDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaRatingDbStorage mpaRatingDbStorage, GenreDbStorage genreDbStorage) {
        super(jdbcTemplate);
        this.mpaRatingDbStorage = mpaRatingDbStorage;
        this.genreDbStorage = genreDbStorage;
    }

    @Override
    public List<Film> getAll() {
        String sql = "select f.film_id, " +
                "f.film_name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.mpa_rating_id, " +
                "mr.mpa_rating_name, " +
                "fg.genre_id, " +
                "g.genre_name, " +
                "fl.user_id " +
                "from films f " +
                "left outer join mpa_ratings mr on f.mpa_rating_id = mr.mpa_rating_id " +
                "left outer join films_genres fg on f.film_id = fg.film_id " +
                "left outer join genres g on fg.genre_id = g.genre_id " +
                "left outer join films_likes fl on f.film_id = fl.film_id";
        return mapFilms(jdbcTemplate.queryForRowSet(sql));
    }

    @Override
    public Film getById(int id) {
        String sql = "select f.film_id, " +
                "f.film_name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.mpa_rating_id, " +
                "mr.mpa_rating_name, " +
                "fg.genre_id, " +
                "g.genre_name, " +
                "fl.user_id " +
                "from films f " +
                "left outer join mpa_ratings mr on f.mpa_rating_id = mr.mpa_rating_id " +
                "left outer join films_genres fg on f.film_id = fg.film_id " +
                "left outer join genres g on fg.genre_id = g.genre_id " +
                "left outer join films_likes fl on f.film_id = fl.film_id " +
                "where f.film_id = ?";
        List<Film> films = mapFilms(jdbcTemplate.queryForRowSet(sql, id));
        if (films.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Film with id %d not found", id));
        } else {
            return films.get(0);
        }
    }

    @Override
    public Film add(Film film) {
        if (film == null) {
            throw new NullPointerException("Can not add null film");
        }
        mpaRatingDbStorage.getById(film.getMpa().getId());
        film.getGenres().forEach(genre -> genreDbStorage.getById(genre.getId()));

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
        mpaRatingDbStorage.getById(film.getMpa().getId());
        film.getGenres().forEach(genre -> genreDbStorage.getById(genre.getId()));

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
        String sql = "select f.film_id, " +
                "f.film_name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.mpa_rating_id, " +
                "mr.mpa_rating_name, " +
                "fg.genre_id, " +
                "g.genre_name, " +
                "fl.user_id " +
                "from films f " +
                "left outer join mpa_ratings mr on f.mpa_rating_id = mr.mpa_rating_id " +
                "left outer join films_genres fg on f.film_id = fg.film_id " +
                "left outer join genres g on fg.genre_id = g.genre_id " +
                "left outer join films_likes fl on f.film_id = fl.film_id " +
                "order by likes_amount desc";
        return mapFilms(jdbcTemplate.queryForRowSet(sql)).stream().limit(count).collect(Collectors.toList());
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

    private List<Film> mapFilms(SqlRowSet rowSet) {
        Map<Integer, Film> films = new LinkedHashMap<>();
        while (rowSet.next()) {
            if (!films.containsKey(rowSet.getInt("film_id"))) {
                MpaRating mpaRating = new MpaRating();
                mpaRating.setId(rowSet.getInt("mpa_rating_id"));
                mpaRating.setName(rowSet.getString("mpa_rating_name"));

                Film film = new Film(
                        rowSet.getString("film_name"),
                        rowSet.getString("description"),
                        LocalDate.parse(rowSet.getString("release_date"), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        rowSet.getInt("duration"),
                        mpaRating
                );
                film.setId(rowSet.getInt("film_id"));

                if (rowSet.getInt("genre_id") != 0) {
                    Genre genre = new Genre();
                    genre.setId(rowSet.getInt("genre_id"));
                    genre.setName(rowSet.getString("genre_name"));
                    film.addGenre(genre);
                }

                if (rowSet.getInt("user_id") != 0) {
                    film.addLikeFromUserId(rowSet.getInt("user_id"));
                }

                films.put(film.getId(), film);
            } else {
                Film film = films.get(rowSet.getInt("film_id"));

                if (rowSet.getInt("genre_id") != 0) {
                    Genre genre = new Genre();
                    genre.setId(rowSet.getInt("genre_id"));
                    genre.setName(rowSet.getString("genre_name"));
                    film.addGenre(genre);
                }

                if (rowSet.getInt("user_id") != 0) {
                    film.addLikeFromUserId(rowSet.getInt("user_id"));
                }
            }
        }
        return new ArrayList<>(films.values());
    }
}