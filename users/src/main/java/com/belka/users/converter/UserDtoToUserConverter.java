package com.belka.users.converter;

import com.belka.core.converter.BelkaConverter;
import com.belka.users.model.User;
import com.belka.users.model.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserDtoToUserConverter implements BelkaConverter<UserDto, User> {
    @Override
    public User convert(UserDto value) {
        return User.builder()
                .id(value.getId())
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
