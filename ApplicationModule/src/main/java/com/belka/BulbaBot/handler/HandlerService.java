package com.belka.BulbaBot.handler;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Flux;

/**
 * a service that aggregates the work of all {@link com.belka.core.handlers.BelkaHandler handlers}
 */
public interface HandlerService {
    /**
     * gives the update to the {@link com.belka.core.handlers.BelkaHandler handlers} for processing
     *
     * @param update {@link Update}
     * @return Flux of the {@link PartialBotApiMethod}
     */
    Flux<PartialBotApiMethod<?>> handle(Update update);
}
