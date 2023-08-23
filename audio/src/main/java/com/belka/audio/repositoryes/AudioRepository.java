package com.belka.audio.repositoryes;

import com.belka.audio.entityes.AudioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AudioRepository extends JpaRepository<AudioEntity, String> {
    @Modifying
    @Query("UPDATE AudioEntity a SET a.isPublic = :flag WHERE a.id = :fileId")
    void changeIsPrivateFlag(boolean flag, String fileId);
}