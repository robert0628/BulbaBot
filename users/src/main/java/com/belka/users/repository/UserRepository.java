package com.belka.users.repository;

import com.belka.users.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * repository for {@link UserEntity user}
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query(value = "SELECT u.* " +
            "FROM users u " +
            "INNER JOIN subscriptions s ON u.id = s.producer " +
            "WHERE s.subscriber = :chatId", nativeQuery = true)
    List<UserEntity> findAllProducers(Long chatId);

    @Query(value = "SELECT u.* " +
            "FROM users u " +
            "INNER JOIN subscriptions s ON u.id = s.subscriber " +
            "WHERE s.subscriber = :chatId", nativeQuery = true)
    List<UserEntity> findAllFollowers(Long chatId);
}
