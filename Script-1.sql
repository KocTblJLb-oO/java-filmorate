DELETE FROM USERS

SELECT COUNT(user_id) FROM USERS where user_id = 1

SELECT * FROM FILM 
                WHERE film_id in (
                SELECT film_id FROM FILM_GENRES
                WHERE genre_id = 1)