package com.belka.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StatsRepository extends JpaRepository<StatsEntity, Long> {
    @Query(value = "SELECT handler_code " +
            "FROM stats " +
            "GROUP BY handler_code " +
            "ORDER BY COUNT(*) " +
            "DESC LIMIT 1",
            nativeQuery = true)
    String getMostPopularRequest();

    @Query(value = "SELECT handler_code " +
            "FROM stats " +
            "WHERE user_id = :chatId " +
            "GROUP BY handler_code " +
            "ORDER BY COUNT(*) " +
            "DESC LIMIT 1",
            nativeQuery = true)
    String getMostPopularRequestByUser(Long chatId);

    long countByHandlerCode(String code);

    long countByUserId(Long chatId);

}
