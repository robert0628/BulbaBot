package com.belka.BulbaBot.repository;

import com.belka.BulbaBot.model.User;
import org.springframework.data.repository.CrudRepository;

/**
 * repository for {@link User user}
 */
public interface UserRepository extends CrudRepository<User, Long> {
}
