package com.belka.users.repository;

import com.belka.users.entities.SubscriptionEntity;
import com.belka.users.entities.SubscriptionKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubscriptionsRepository extends JpaRepository<SubscriptionEntity, SubscriptionKey> {

    @Query(value = "SELECT subscriber FROM subscriptions WHERE producer = :chatId", nativeQuery = true)
    List<Long> findAllFollowersID(Long chatId);

    @Modifying(flushAutomatically = true)
    @Query(value = "DELETE FROM subscriptions WHERE producer = :producerId AND subscriber = :subscriberId", nativeQuery = true)
    void deleteByProducerAndSubscriber(Long producerId, Long subscriberId);
}