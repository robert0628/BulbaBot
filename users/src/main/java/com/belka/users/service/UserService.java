package com.belka.users.service;

import com.belka.users.model.UserDto;

import java.util.Collection;

/**
 * service for interaction with {@link com.belka.users.model.UserEntity users}
 */
public interface UserService {
    Collection<UserDto> getAll();

    void save(UserDto userDto);

    boolean existsById(Long chatId);
}
