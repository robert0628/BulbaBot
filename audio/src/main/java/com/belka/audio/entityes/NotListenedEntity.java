package com.belka.audio.entityes;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "not_listened")
@Entity
public class NotListenedEntity {
    @Id
    @Column(name = "subscriber")
    private Long subscriber;

    @Column(name = "audio_id")
    private String audioId;
}
