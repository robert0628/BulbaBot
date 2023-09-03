package com.belka.users.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

@Data
@Builder
public class UserDto {
    private Long id;
    private String firstname;
    private String lastname;
    private String username;
    private OffsetDateTime registeredAt;
}
