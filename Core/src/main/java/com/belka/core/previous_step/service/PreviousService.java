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
     * get {@link com.belka.core.previous_step.entity.PreviousStepEntity previouse id}
     *
     * @param chatId user's id
     * @return id of the previous step
     */
    Integer getPreviousIdByUserId(Long chatId);
}
