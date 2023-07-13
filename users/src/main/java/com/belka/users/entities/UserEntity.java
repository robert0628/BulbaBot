package com.belka.users.entities;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * bot user
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
@Entity
public class UserEntity {
    @Id
    private Long id;
    private String firstname;
    private String lastname;
    private String username;
    private Timestamp registeredAt;
}
