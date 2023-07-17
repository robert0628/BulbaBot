package com.belka.core.previous_step.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PreviousStepDto {
    private Long userId;
    private String previousStep;
    private String nextStep;
}
