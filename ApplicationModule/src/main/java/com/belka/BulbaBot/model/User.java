package com.belka.BulbaBot.model;

import lombok.*;

import javax.persistence.*;
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
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String firstname;
    private String lastname;
    private String username;
    private Timestamp registeredAt;

}
