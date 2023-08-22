package com.belka.audio.repositoryes;

import com.belka.audio.entityes.AudioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AudioRepository extends JpaRepository<AudioEntity, String> {
    @Query(value = "UPDATE audio\n" +
            "SET is_public = :flag" +
            "WHERE id = :fileId", nativeQuery = true)
    void changeIsPrivateFlag(boolean flag, String fileId);
}