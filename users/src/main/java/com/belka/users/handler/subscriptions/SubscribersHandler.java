package com.belka.users.handler.subscriptions;

import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import reactor.core.publisher.Flux;

@AllArgsConstructor
public class SubscribersHandler extends AbstractBelkaHandler {
    public final static String CODE = "/Subscribers";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = "";

    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        //todo add logic to work with user's subscribers
        return null;
    }
}