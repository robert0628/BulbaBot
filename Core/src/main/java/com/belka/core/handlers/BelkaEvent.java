package com.belka.core.handlers;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Update;

@Data
@Builder
public class BelkaEvent {
    private final Update update;
    private final Long chatId;
    private final String previous_step;
    private final String data;
    private final String message;
}
