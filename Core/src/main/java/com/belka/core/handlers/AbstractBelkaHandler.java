package com.belka.core.handlers;

import com.belka.core.BelkaSendMessage;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import reactor.core.publisher.Flux;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Setter
public abstract class AbstractBelkaHandler implements BelkaHandler {
    private final static String TIMEOUT_MESSAGE = "sorry, it's tooooo long processing, try again or later";
    private final static String EXCEPTION_MESSAGE = "something was wrong and your request has been interrupted, try again or later";
    @Value("${bot.handler.timeout}")
    private Integer timeout;

    @Autowired
    public void setBelkaSendMessage(BelkaSendMessage belkaSendMessage) {
        this.belkaSendMessage = belkaSendMessage;
    }

    private BelkaSendMessage belkaSendMessage;

    @Override
    abstract public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event);

    protected SendMessage sendMessage(Long chatId, String answer) {
        return belkaSendMessage.sendMessage(chatId, answer);
    }

    protected PartialBotApiMethod<?> sendImageFromUrl(String url, Long chatId) {
        return belkaSendMessage.sendImageFromUrl(url, chatId);
    }

    protected PartialBotApiMethod<?> editMessage(SendMessage message, String text) {
        return belkaSendMessage.editMessage(message, text);
    }

    protected Flux<PartialBotApiMethod<?>> future(CompletableFuture<Flux<PartialBotApiMethod<?>>> future, Long chatId) {
        try {
            return future.get(timeout, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            return Flux.just(sendMessage(chatId, TIMEOUT_MESSAGE));
        } catch (InterruptedException | ExecutionException e) {
            return Flux.just(sendMessage(chatId, EXCEPTION_MESSAGE));
        }
    }
}
