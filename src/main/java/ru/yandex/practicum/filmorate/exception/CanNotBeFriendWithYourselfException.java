package ru.yandex.practicum.filmorate.exception;

public class CanNotBeFriendWithYourselfException extends RuntimeException{
    public CanNotBeFriendWithYourselfException(String message) {
        super(message);
    }
}