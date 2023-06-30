package com.belka.core.handlers;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import reactor.core.publisher.Flux;

public interface BelkaHandler {
    Flux<PartialBotApiMethod<?>> handle(BelkaEvent event);
}
