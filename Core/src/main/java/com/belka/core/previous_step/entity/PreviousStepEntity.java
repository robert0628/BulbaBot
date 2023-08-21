package com.belka.core.previous_step.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "previous_step")
public class PreviousStepEntity {
    @Id
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "previous_step")
    private String previousStep;
    @Column(name = "next_step")
    private String nextStep;
    private String data;
}
