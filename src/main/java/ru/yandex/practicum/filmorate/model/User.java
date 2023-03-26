package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.valid.NoSpaces;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import java.time.LocalDate;

@Data
public class User implements HasId {
    private int id = 0;
    @NotBlank
    @Email
    private final String email;
    @NotBlank
    @NoSpaces
    private final String login;
    private String name;
    @NotNull
    @Past
    private final LocalDate birthday;

    public void fillName() {
        if (name == null || name.isBlank()) {
            name = login;
        }
    }
}