package com.belka.users.repository;

import com.belka.users.entities.SubscriptionEntity;
import com.belka.users.entities.SubscriptionKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionsRepository extends JpaRepository<SubscriptionEntity, SubscriptionKey> {
}
