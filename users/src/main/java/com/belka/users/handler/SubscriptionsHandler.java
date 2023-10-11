package com.belka.users.handler;

import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import reactor.core.publisher.Flux;

@AllArgsConstructor
public class SubscriptionsHandler extends AbstractBelkaHandler {
    public final static String CODE = "/Subscriptions";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = "";
    private final static String ANSWER = "";


    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        //todo add logic to work with user's subscriptions
        return null;
    }
}
