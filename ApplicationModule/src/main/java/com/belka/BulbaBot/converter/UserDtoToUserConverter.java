package com.belka.BulbaBot.converter;

import com.belka.BulbaBot.model.User;
import com.belka.BulbaBot.model.UserDto;
import com.belka.core.converter.BelkaConverter;
import org.springframework.stereotype.Component;

@Component
public class UserDtoToUserConverter implements BelkaConverter<UserDto, User> {
    @Override
    public User convert(UserDto value) {
        return User.builder()
                .firstname(value.getFirstname())
                .lastname(value.getLastname())
                .username(value.getUsername())
                .registeredAt(value.getRegisteredAt())
                .build();
    }

    @Override
    public Class<User> getOutputType() {
        return User.class;
    }

    @Override
    public Class<UserDto> getInputType() {
        return UserDto.class;
    }
}
