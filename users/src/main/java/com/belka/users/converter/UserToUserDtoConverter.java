package com.belka.users.converter;

import com.belka.core.converter.BelkaConverter;
import com.belka.users.entities.UserEntity;
import com.belka.users.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserToUserDtoConverter implements BelkaConverter<UserEntity, UserDto> {
    @Override
    public UserDto convert(UserEntity value) {
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
    public Class<UserEntity> getInputType() {
        return UserEntity.class;
    }
}
