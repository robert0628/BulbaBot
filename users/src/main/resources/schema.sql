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
    FOREIGN KEY (producer) REFERENCES users (id),
    FOREIGN KEY (subscriber) REFERENCES users (id)
);