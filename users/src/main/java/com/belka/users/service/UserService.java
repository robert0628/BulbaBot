package com.belka.users.service;

import com.belka.users.model.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getAll();

    UserDto getUser(Long chatId);

    void save(UserDto userDto);

    boolean existsById(Long chatId);
}
