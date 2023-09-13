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