package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.valid.NoSpaces;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
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
    private final Set<Integer> friendsIds = new HashSet<>();

    public void fillName() {
        if (name == null || name.isBlank()) {
            name = login;
        }
    }

    public void addFriendId(int id) {
        friendsIds.add(id);
    }

    public void deleteFriendId(int id) {
        friendsIds.remove(id);
    }

    public void deleteAllFriends() {
        friendsIds.clear();
    }
}