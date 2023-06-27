package com.belka.core.converter;

import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.entity.PreviousStep;
import org.springframework.stereotype.Component;

@Component
public class PreviousStepDTOConverterToEntity implements BelkaConverter<PreviousStepDto, PreviousStep> {
    @Override
    public PreviousStep convert(PreviousStepDto value) {
        return PreviousStep.builder()
                .previousStep(value.getPreviousStep())
                .userId(value.getUserId())
                .build();
    }

    @Override
    public Class<PreviousStep> getOutputType() {
        return PreviousStep.class;
    }

    @Override
    public Class<PreviousStepDto> getInputType() {
        return PreviousStepDto.class;
    }
}
