package com.belka.newDiary.repository;

import com.belka.newDiary.entity.DiaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<DiaryEntity, Long> {
    Optional<DiaryEntity> getByDateAndUserId(Date date, Long userId);
}
