package com.belka.core.previous_step.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Builder
@AllArgsConstructor
@Setter
@Getter
@Table(name = "previous_step")
public class PreviousStep {
    @Id
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "previous_step")
    private String previousStep;

    public PreviousStep() {

    }
}
