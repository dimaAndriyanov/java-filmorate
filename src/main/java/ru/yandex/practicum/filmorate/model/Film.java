package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.valid.NotBefore_1895_12_28;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import java.time.LocalDate;

@Data
public class Film implements HasId{
    private int id = 0;
    @NotBlank
    private final String name;
    @NotBlank
    @Size(max = 200)
    private final String description;
    @NotNull
    @NotBefore_1895_12_28
    private final LocalDate releaseDate;
    @Positive
    private final int duration;
}