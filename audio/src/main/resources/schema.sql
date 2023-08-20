CREATE TABLE IF NOT EXISTS audio
(
    id        varchar PRIMARY KEY,
    user_id   bigint,
    date      date,
    is_public boolean
);