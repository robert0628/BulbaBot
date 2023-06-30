package com.belka.users.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class UserDto {
    private Long id;
    private String firstname;
    private String lastname;
    private String username;
    private Timestamp registeredAt;
}
