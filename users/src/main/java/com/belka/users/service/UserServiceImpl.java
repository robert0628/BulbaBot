package com.belka.users.service;

import com.belka.core.converter.ConverterService;
import com.belka.users.model.UserEntity;
import com.belka.users.model.UserDto;
import com.belka.users.repository.UserRepository;
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
                .map(userEntity -> converterService.ConvertTo(UserDto.class, userEntity))
                .collect(Collectors.toList());
    }

    @Override
    public void save(UserDto userDto) {
        userRepository.save(converterService.ConvertTo(UserEntity.class, userDto));
    }

    @Override
    public boolean existsById(Long chatId) {
        return userRepository.existsById(chatId);
    }
}
