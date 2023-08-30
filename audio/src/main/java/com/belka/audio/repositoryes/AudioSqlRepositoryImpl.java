package com.belka.audio.repositoryes;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
@Repository
public class AudioSqlRepositoryImpl implements AudioSqlRepository {
    private final JdbcTemplate jdbcTemplate;
    /**
     * This query will select all subscribers and all new audio recordings created for the current day
     * and add them to the not_listed table
     */
    private final static String sqlQueryFill = """
            INSERT INTO not_listened (subscriber, audio_id)
            SELECT s.subscriber, a.id
            FROM subscriptions s
            JOIN audio a
            ON s.producer = a.user_id
            WHERE a.date = CURRENT_DATE - INTERVAL '1 DAY'
            ON CONFLICT DO NOTHING
            """;

    private final static String sqlQueryDelete = "DELETE FROM not_listened WHERE subscriber = ? AND audio_id = ?";

    @Override
    public void fillNotListened() {
        jdbcTemplate.execute(sqlQueryFill);
    }

    @Override
    public void deleteFromNotListened(Long userId, String fileId) {
        jdbcTemplate.update(sqlQueryDelete, userId, fileId);
    }
}
