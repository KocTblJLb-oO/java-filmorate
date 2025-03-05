package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    long id;

    @NotBlank
    String name;

    @Size(min = 1, max = 200, message = "Максимальная длина описания — 200 символов")
    String description;

    LocalDate releaseDate;

    @Min(1)
    int duration;
}
