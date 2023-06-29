package com.belka.BulbaBot.service;

import com.belka.BulbaBot.model.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getAll();

    UserDto getUser(Long chatId);

    void save(UserDto userDto);

    boolean existsById(Long chatId);
}
