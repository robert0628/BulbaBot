package com.belka.BulbaBot.handler;

import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import reactor.core.publisher.Flux;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class TestHandler extends AbstractBelkaHandler {

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (event.isHasText() && event.getText().equals("qwe")) {
                for (long i = 1; i < 40000000000L; i++) {
                    i++;
                }
                log.info("отсчет закончен");
                return Flux.just(sendMessage(event.getChatId(), "qwe"));
            }
            log.info("второй пошел");
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }

}


