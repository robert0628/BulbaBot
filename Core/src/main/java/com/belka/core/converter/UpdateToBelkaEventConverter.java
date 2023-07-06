package com.belka.core.converter;

import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.service.PreviousService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class UpdateToBelkaEventConverter implements BelkaConverter<Update, BelkaEvent> {

    private PreviousService previousService;

    @Autowired
    public void setPreviousService(@Lazy PreviousService previousService) {
        this.previousService = previousService;
    }

    @Override
    public BelkaEvent convert(Update value) {
        String text = null;
        String data = null;
        Long chatId = getChatId(value);
        Integer updateId = value.getUpdateId();
        String previousStep = "";
        String previousStepFromDB = previousService.getPreviousStep(chatId);
        if (previousStepFromDB != null) {
            previousStep = previousStepFromDB;
        }
        boolean hasMessage = value.hasMessage();
        boolean hasText = hasMessage && value.getMessage().hasText();
        boolean hasCallbackQuery = value.hasCallbackQuery();

        if (hasText) {
            text = value.getMessage().getText();
        }
        if (hasCallbackQuery) {
            data = value.getCallbackQuery().getData();
        }
        return BelkaEvent.builder()
                .update(value)
                .updateId(updateId)
                .data(data)
                .chatId(chatId)
                .text(text)
                .previous_step(previousStep)
                .hasMessage(hasMessage)
                .hasCallbackQuery(hasCallbackQuery)
                .hasText(hasText)
                .build();
    }

    @Override
    public Class<BelkaEvent> getOutputType() {
        return BelkaEvent.class;
    }

    @Override
    public Class<Update> getInputType() {
        return Update.class;
    }

    public Long getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        }
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        throw new RuntimeException("received an Update without an id");
    }
}
