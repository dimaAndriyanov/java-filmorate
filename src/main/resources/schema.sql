CREATE TABLE PUBLIC.USERS (
	USER_ID INTEGER NOT NULL AUTO_INCREMENT,
	USER_NAME CHARACTER VARYING(255) NOT NULL,
	EMAIL CHARACTER VARYING(50) NOT NULL,
	LOGIN CHARACTER VARYING(20) NOT NULL,
	BIRTHDAY DATE NOT NULL,
	CONSTRAINT USERS_PK PRIMARY KEY (USER_ID)
);
CREATE UNIQUE INDEX USERS_EMAIL_IDX ON PUBLIC.USERS (EMAIL);
CREATE UNIQUE INDEX USERS_LOGIN_IDX ON PUBLIC.USERS (LOGIN);

CREATE TABLE PUBLIC.USERS_FRIENDS (
	USER_ID INTEGER NOT NULL,
	FRIEND_ID INTEGER NOT NULL,
	CONSTRAINT USERS_FRIENDS_PK PRIMARY KEY (USER_ID,FRIEND_ID),
	CONSTRAINT USERS_FRIENDS_FK FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(USER_ID) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT USERS_FRIENDS_FK_1 FOREIGN KEY (FRIEND_ID) REFERENCES PUBLIC.USERS(USER_ID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE PUBLIC.GENRES (
    GENRE_ID INTEGER NOT NULL AUTO_INCREMENT,
    GENRE_NAME CHARACTER VARYING(50) NOT NULL,
    CONSTRAINT GENRES_PK PRIMARY KEY (GENRE_ID)
);

CREATE TABLE PUBLIC.MPA_RATINGS (
    MPA_RATING_ID INTEGER NOT NULL AUTO_INCREMENT,
    MPA_RATING_NAME CHARACTER VARYING(5) NOT NULL,
    CONSTRAINT MPA_RATING_PK PRIMARY KEY (MPA_RATING_ID)
);

CREATE TABLE PUBLIC.FILMS (
	FILM_ID INTEGER NOT NULL AUTO_INCREMENT,
	FILM_NAME CHARACTER VARYING(255) NOT NULL,
	DESCRIPTION CHARACTER VARYING(200) NOT NULL,
	RELEASE_DATE DATE NOT NULL,
	DURATION INTEGER NOT NULL,
	LIKES_AMOUNT INTEGER NOT NULL,
	MPA_RATING_ID INTEGER NOT NULL,
	CONSTRAINT FILMS_PK PRIMARY KEY (FILM_ID),
	CONSTRAINT FILMS_FK FOREIGN KEY (MPA_RATING_ID) REFERENCES PUBLIC.MPA_RATINGS(MPA_RATING_ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE PUBLIC.FILMS_GENRES (
    FILM_ID INTEGER NOT NULL,
    GENRE_ID INTEGER NOT NULL,
    CONSTRAINT FILMS_GENRES_PK PRIMARY KEY (FILM_ID,GENRE_ID),
    CONSTRAINT FILMS_GENRES_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILMS(FILM_ID) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FILMS_GENRES_FK_1 FOREIGN KEY (GENRE_ID) REFERENCES PUBLIC.GENRES(GENRE_ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE PUBLIC.FILMS_LIKES (
    FILM_ID INTEGER NOT NULL,
    USER_ID INTEGER NOT NULL,
    CONSTRAINT FILMS_LIKES_PK PRIMARY KEY (FILM_ID,USER_ID),
    CONSTRAINT FILMS_LIKES_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILMS(FILM_ID) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FILMS_LIKES_FK_1 FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(USER_ID) ON DELETE CASCADE ON UPDATE CASCADE
);