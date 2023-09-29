CREATE TABLE IF NOT EXISTS users
(
    id            bigint PRIMARY KEY,
    firstname     varchar,
    lastname      varchar,
    registered_at timestamptz,
    username      varchar
);

create table if not exists subscriptions
(
    producer   bigint not null,
    subscriber bigint not null,
    PRIMARY KEY (producer, subscriber),
    FOREIGN KEY (producer) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (subscriber) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS weather_history
(
    id       bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    temp     int,
    date     date,
    city     varchar
);

CREATE TABLE IF NOT EXISTS previous_step
(
    user_id       bigint PRIMARY KEY,
    previous_step varchar,
    next_step     varchar,
    data          varchar,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS audio
(
    id        varchar PRIMARY KEY,
    user_id   bigint,
    date      date,
    is_public boolean,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS not_listened
(
    subscriber bigint,
    audio_id   varchar,
    PRIMARY KEY (subscriber, audio_id),
    FOREIGN KEY (subscriber) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (audio_id) REFERENCES audio (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS stats
(
    id           bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id      bigint,
    request_time timestamptz,
    handler_code varchar,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Diary
(
    id      bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id bigint,
    note    text,
    date    date,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

