CREATE TABLE IF NOT EXISTS stats
(
    user_id   bigint PRIMARY KEY,
    request_time timestamp,
    handler_code     varchar
);
