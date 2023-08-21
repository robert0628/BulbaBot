package com.belka.core.previous_step.service;

import com.belka.core.previous_step.dto.PreviousStepDto;

/**
 * service for tracking the user's previous steps
 */
public interface PreviousService {
    /**
     * saving {@link com.belka.core.previous_step.entity.PreviousStepEntity previouse step}
     *
     * @param dto {@link com.belka.core.previous_step.entity.PreviousStepEntity previouse step's} dto
     */
    void save(PreviousStepDto dto);

    /**
     * get {@link com.belka.core.previous_step.entity.PreviousStepEntity previouse step}
     *
     * @param chatId user's id
     * @return code of the previous step
     */
    String getPreviousStep(Long chatId);

    /**
     * get {@link com.belka.core.previous_step.entity.PreviousStepEntity next step}
     *
     * @param chatId user's id
     * @return code of the next step
     */
    String getNextStep(Long chatId);

    /**
     * get get {@link com.belka.core.previous_step.entity.PreviousStepEntity data}
     *
     * @param chatId user's id
     * @return data from the previous step
     */
    String getData(Long chatId);
}
