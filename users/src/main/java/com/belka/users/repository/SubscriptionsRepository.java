package com.belka.users.repository;

import com.belka.users.entities.SubscriptionEntity;
import com.belka.users.entities.SubscriptionKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface SubscriptionsRepository extends JpaRepository<SubscriptionEntity, SubscriptionKey> {
    @Query(value = "SELECT producer FROM subscriptions WHERE subscriber = :chatId", nativeQuery = true)
    List<Long> findAllProducersID(Long chatId);

    @Query(value = "SELECT subscriber FROM subscriptions WHERE producer = :chatId", nativeQuery = true)
    List<Long> findAllFollowersID(Long chatId);

    @Modifying
    @Query("UPDATE SubscriptionEntity s SET s.date = :date WHERE s.producer.id = :producerId and s.subscriber.id = :subscriberId")
    void setPullDate(LocalDate date, Long producerId, Long subscriberId);
}
