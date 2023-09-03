CREATE TABLE IF NOT EXISTS stats
(
    id           bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id      bigint,
    request_time timestamptz,
    handler_code varchar,
    FOREIGN KEY (user_id) REFERENCES users (id)
);
