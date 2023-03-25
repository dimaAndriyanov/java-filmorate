package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class User implements HasId{
    private int id = 0;
    private final String email;
    private final String login;
    private String name;
    private final LocalDate birthday;
}