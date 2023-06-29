package com.belka.BulbaBot.converter;

import com.belka.BulbaBot.model.User;
import com.belka.BulbaBot.model.UserDto;
import com.belka.core.converter.BelkaConverter;
import org.springframework.stereotype.Component;

@Component
public class UserToUserDtoConverter implements BelkaConverter<User, UserDto> {
    @Override
    public UserDto convert(User value) {
        return UserDto.builder()
                .id(value.getId())
                .firstname(value.getFirstname())
                .lastname(value.getLastname())
                .username(value.getUsername())
                .registeredAt(value.getRegisteredAt())
                .build();
    }

    @Override
    public Class<UserDto> getOutputType() {
        return UserDto.class;
    }

    @Override
    public Class<User> getInputType() {
        return User.class;
    }
}
