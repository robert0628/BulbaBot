package com.belka.core.previous_step;

import com.belka.core.converter.ConverterService;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.entity.PreviousStep;
import com.belka.core.previous_step.repositoty.PreviousStepRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PreviousService {
    private final PreviousStepRepository repository;
    private final ConverterService converterService;

    public void save(PreviousStepDto dto) {
        repository.save(converterService.ConvertTo(PreviousStep.class, dto));
    }

    public String getPreviousStep(Long chatId) {
        return repository.getPreviousStepByUserId(chatId);
    }
}
