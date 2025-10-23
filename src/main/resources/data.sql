DELETE FROM review_likes;
DELETE FROM film_likes;
DELETE FROM film_genres;
DELETE FROM friendships;
DELETE FROM directors_films;
DELETE FROM reviews;
DELETE FROM feed_events;

DELETE FROM films;
DELETE FROM users;
DELETE FROM genres;
DELETE FROM directors;
DELETE FROM mpa_ratings;

-- Сбрасываем автоинкремент
ALTER TABLE users ALTER COLUMN id RESTART WITH 1;
ALTER TABLE films ALTER COLUMN id RESTART WITH 1;
ALTER TABLE directors ALTER COLUMN id RESTART WITH 1;
ALTER TABLE mpa_ratings ALTER COLUMN id RESTART WITH 1;
ALTER TABLE genres ALTER COLUMN id RESTART WITH 1;
ALTER TABLE reviews ALTER COLUMN review_id RESTART WITH 1;
ALTER TABLE feed_events ALTER COLUMN event_id RESTART WITH 1;


MERGE INTO mpa_ratings (id, name) KEY(id) VALUES
(1, 'G'),
(2, 'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');

MERGE INTO genres (id, name) KEY(id) VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');