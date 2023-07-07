package com.belka.stats;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StatsRepository extends JpaRepository<StatsEntity, Long> {
}
