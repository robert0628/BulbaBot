package com.belka.users.converter;

import com.belka.core.converter.BelkaConverter;
import com.belka.users.model.User;
import com.belka.users.model.UserDto;
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
