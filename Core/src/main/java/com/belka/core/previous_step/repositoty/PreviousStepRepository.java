package com.belka.core.previous_step.repositoty;

import com.belka.core.previous_step.entity.PreviousStepEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PreviousStepRepository extends JpaRepository<PreviousStepEntity, Long> {
    @Query(value = "SELECT previous_step FROM previous_step WHERE user_id = :userId", nativeQuery = true)
    String getPreviousStepByUserId(Long userId);

    @Query(value = "SELECT next_step FROM previous_step WHERE user_id = :userId", nativeQuery = true)
    String getNextStepByUserId(Long userId);
    @Query(value = "SELECT data FROM previous_step WHERE user_id = :userId", nativeQuery = true)
    String getDataByUserId(Long userId);
}
