package com.belka.users.repository;

import com.belka.users.entities.SubscriptionEntity;
import com.belka.users.entities.SubscriptionKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubscriptionsRepository extends JpaRepository<SubscriptionEntity, SubscriptionKey> {
    @Query(value = "SELECT producer FROM subscriptions WHERE subscriber = :chatId", nativeQuery = true)
    List<Long> findAllProducersID(Long chatId);
}
