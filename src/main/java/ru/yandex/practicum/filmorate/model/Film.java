package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Film implements HasId{
    private int id = 0;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final int duration;
}