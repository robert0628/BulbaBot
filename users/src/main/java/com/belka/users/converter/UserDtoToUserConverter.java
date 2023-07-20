package com.belka.users.converter;

import com.belka.core.converter.BelkaConverter;
import com.belka.users.entities.UserEntity;
import com.belka.users.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserDtoToUserConverter implements BelkaConverter<UserDto, UserEntity> {
    @Override
    public UserEntity convert(UserDto value) {
        return UserEntity.builder()
                .id(value.getId())
                .firstname(value.getFirstname())
                .lastname(value.getLastname())
                .username(value.getUsername())
                .registeredAt(value.getRegisteredAt())
                .build();
    }

    @Override
    public Class<UserEntity> getOutputType() {
        return UserEntity.class;
    }

    @Override
    public Class<UserDto> getInputType() {
        return UserDto.class;
    }
}
