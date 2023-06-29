package com.belka.BulbaBot.service;

import com.belka.BulbaBot.model.User;
import com.belka.BulbaBot.model.UserDto;
import com.belka.BulbaBot.repository.UserRepository;
import com.belka.core.converter.ConverterService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ConverterService converterService;

    @Override
    public Collection<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(user -> converterService.ConvertTo(UserDto.class, user))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Long chatId) {
        return converterService.ConvertTo(UserDto.class, userRepository.findById(chatId));
    }

    @Override
    public void save(UserDto userDto) {
        userRepository.save(converterService.ConvertTo(User.class, userDto));
    }

    @Override
    public boolean existsById(Long chatId) {
        return userRepository.existsById(chatId);
    }
}
