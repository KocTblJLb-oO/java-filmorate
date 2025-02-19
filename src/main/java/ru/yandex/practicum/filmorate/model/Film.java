package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * Film.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Film {
    int id;

    @NotBlank
    String name;

    String description;

    LocalDate releaseDate;

    @Min(1)
    int duration;
}
