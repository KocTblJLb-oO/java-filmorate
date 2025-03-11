package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {
    long id;

    @Email
    String email;

    @NotBlank
    String login;

    String name;

    @PastOrPresent(message = "Дата рождения должна быть в прошлом или настоящем")
    LocalDate birthday;

    Set<Long> friends = new HashSet<>();
}