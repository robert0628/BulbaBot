package com.belka.core.handlers;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

public interface BelkaHandler {
    PartialBotApiMethod<?> handle(BelkaEvent event);
}
