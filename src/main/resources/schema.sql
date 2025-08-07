CREATE TABLE IF NOT EXISTS Genres (
    genre_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS Mpa (
    mpa_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS Film (
    film_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    release_date DATE NOT NULL,
    duration INT NOT NULL,
    mpa_id INT,
    FOREIGN KEY (mpa_id) REFERENCES Mpa(mpa_id)
);

CREATE TABLE IF NOT EXISTS FILM_GENRES (
    film_id INT NOT NULL,
    genre_id INT NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES FILM(film_id),
    FOREIGN KEY (genre_id) REFERENCES GENRES(genre_id)
);

CREATE TABLE IF NOT EXISTS Users (
    user_id INT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    login VARCHAR(100) NOT NULL,
    name VARCHAR(100) ,
    birth_day DATE
);

CREATE TABLE IF NOT EXISTS Likes (
    film_id INT NOT NULL,
    user_id INT NOT NULL,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES Film(film_id),
    FOREIGN KEY (user_id) REFERENCES Users (user_id)
);

CREATE TABLE IF NOT EXISTS Friends (
    user1_id INT NOT NULL,
    user2_id INT NOT NULL,
    status boolean NOT NULL,
    PRIMARY KEY (user1_id, user2_id),
    FOREIGN KEY (user1_id) REFERENCES Users(user_id),
    FOREIGN KEY (user2_id) REFERENCES Users(user_id)
);