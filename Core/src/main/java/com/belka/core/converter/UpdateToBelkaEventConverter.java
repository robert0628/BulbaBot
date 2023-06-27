package com.belka.core.converter;

import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.PreviousService;
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
        String message = null;
        String data = null;
        Long chatId = getChatId(value);
        String previousStep = previousService.getPreviousStep(chatId);
        if (value.hasMessage()) {
            message = value.getMessage().getText();
        }
        if (value.hasCallbackQuery()) {
            data = value.getCallbackQuery().getData();
        }
        return BelkaEvent.builder()
                .update(value)
                .data(data)
                .chatId(chatId)
                .message(message)
                .previous_step(previousStep)
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
        return null;
    }
}
