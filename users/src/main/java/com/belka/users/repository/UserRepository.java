package com.belka.users.repository;

import com.belka.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * repository for {@link com.belka.users.model.User user}
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
