package com.belka.core.handlers;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import reactor.core.publisher.Flux;

/**
 * a handler that handle {@link BelkaEvent events} from user
 */
public interface BelkaHandler {
    /**
     * handle {@link BelkaEvent event} from user
     *
     * @param event {@link BelkaEvent}
     * @return collection of responses
     */
    Flux<PartialBotApiMethod<?>> handle(BelkaEvent event);
}
