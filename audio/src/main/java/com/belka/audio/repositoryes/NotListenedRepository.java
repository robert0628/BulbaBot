package com.belka.audio.repositoryes;

import com.belka.audio.entityes.NotListenedEntity;
import com.belka.audio.entityes.NotListenedKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotListenedRepository extends JpaRepository<NotListenedEntity, NotListenedKey> {
    @Query(value = """
            SELECT n.subscriber, n.audio_id
            FROM not_listened n
            JOIN audio a
            ON n.audio_id = a.id
            ORDER BY a.date
            ASC LIMIT 1
            """, nativeQuery = true)
    NotListenedEntity getOldestAudio();
}