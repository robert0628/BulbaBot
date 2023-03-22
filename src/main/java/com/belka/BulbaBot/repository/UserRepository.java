package com.belka.BulbaBot.repository;

import com.belka.BulbaBot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * repository for {@link User user}
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
