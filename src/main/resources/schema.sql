CREATE TABLE IF NOT EXISTS users
(
    id            bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    firstname     varchar,
    lastname      varchar,
    registered_at timestamp,
    username      varchar
);

CREATE TABLE IF NOT EXISTS weather_history
(
    id       bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    temp     int,
    lastname varchar,
    date     date,
    city     varchar
)
