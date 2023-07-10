package com.belka.users.repository;

import com.belka.users.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * repository for {@link UserEntity user}
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
