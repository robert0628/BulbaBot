package com.belka.newDiary.repository;

import com.belka.newDiary.entity.DiaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<DiaryEntity, Long> {
    Optional<DiaryEntity> getByDateAndUserId(LocalDate date, Long userId);

    @Query(value = "SELECT note FROM diary WHERE user_id = :userId and date = :date", nativeQuery = true)
    Optional<String> findNoteByUserIdAndDate(Long userId, LocalDate date);

    boolean existsByUserIdAndDate(Long userId, LocalDate date);
}
