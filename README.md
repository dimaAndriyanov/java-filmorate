# java-filmorate
## База данных
### Схема
![Graphical representation of Database](documents/DB_scheme.png)
### Пояснение
* В таблицах films и users хранятся данные о фильмах и пользователях соответственно.
* В таблицах genres и mpa_ratings хранятся перечни жанров и рейтингов Ассоциации кинокомпаний соответственно.
* В таблицах films_genres и films_likes хранятся связи между фильмами и их жанрами, а также между фильмами и
пользователями, поставившими этим фильмам лайк.
* В таблице users_friends хранятся связи между пользователями и их друзьями
### Примеры запросов
* Запрос на получение всех фильмов
```
SELECT *
FROM films;
```
* Запрос на получение конкретного фильма по `id`
```
SELECT *
FROM films
WHERE film_id = id;
```
* Запрос на получение `count` самых популярных фильмов
```
SELECT *
FROM films
ORDER BY likes_amount DESC
LIMIT count;
```
* Запрос на получение списка друзей конкретного пользователя с `id`
```
SELECT *
FROM users
WHERE user_id IN (SELECT friend_id
                  FROM users_friends
                  WHERE user_id = id);
```
* Запрос на получения общего списка друзей двух пользователей с `id1` и `id2`
```
SELECT *
FROM users
WHERE user_id IN (SELECT friend_id
                  FROM users_friends
                  WHERE user_id = id1
                  INTERSECT
                  SELECT friend_id
                  FROM users_friends
                  WHERE user_id = id2);
```