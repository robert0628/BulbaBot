package com.belka.audio.repositoryes;

import com.belka.audio.entityes.AudioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AudioRepository extends JpaRepository<AudioEntity, String> {
    @Modifying
    @Query("UPDATE AudioEntity a SET a.isPublic = :flag WHERE a.id = :fileId")
    void changeIsPrivateFlag(boolean flag, String fileId);

    boolean existsByDateAndUserId(LocalDate date, Long userId);

    @Query("select a.id from AudioEntity a WHERE a.date = :date and a.userId = :userId")
    List<String> getAllIdByDateAndUserId(LocalDate date, Long userId);

    @Query("select a.id from AudioEntity a WHERE a.userId = :userId")
    List<String> getAllIdByUserId(Long userId);
}