CREATE TABLE IF NOT EXISTS users
(
    id            bigint PRIMARY KEY,
    firstname     varchar,
    lastname      varchar,
    registered_at timestamp,
    username      varchar
);