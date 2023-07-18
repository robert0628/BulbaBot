package com.belka.core.handlers;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Event from telegram API
 */
@Data
@Builder
public class BelkaEvent {
    /**
     * Telegram update object
     */
    private final Update update;
    /**
     * user's id
     */
    private final Long chatId;
    /**
     * {@link Update update's } id
     */
    private final Integer updateId;
    /**
     * previous handler's CODE
     */
    private final String previous_step;
    private final String code;
    private final String data;
    private final String text;
    private final boolean hasMessage;
    private final boolean hasText;
    private final boolean hasCallbackQuery;
}
