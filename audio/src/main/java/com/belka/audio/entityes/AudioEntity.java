package com.belka.audio.entityes;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "audio")
@Entity
public class AudioEntity {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "is_public")
    private boolean isPublic;
    private LocalDate date;
}
