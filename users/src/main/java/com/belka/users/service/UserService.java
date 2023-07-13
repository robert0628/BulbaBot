package com.belka.users.service;

import com.belka.users.entities.UserEntity;
import com.belka.users.model.UserDto;

import java.util.Collection;

/**
 * service for interaction with {@link UserEntity users}
 */
public interface UserService {
    Collection<UserDto> getAll();

    void save(UserDto userDto);

    boolean existsById(Long chatId);

    UserDto getUserById(Long chatId);

    void toSubscribe(Long chatId, Long producerId);

    Collection<UserDto> getAllById(Collection<Long> producersID);
}
