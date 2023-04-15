package ru.yandex.practicum.filmorate.exception;

public class LoginAlreadyInUseException extends RuntimeException {
    public LoginAlreadyInUseException(String message) {
        super(message);
    }
}