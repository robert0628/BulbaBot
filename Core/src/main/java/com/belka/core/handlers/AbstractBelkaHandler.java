package com.belka.core.handlers;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import reactor.core.publisher.Flux;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractBelkaHandler implements BelkaHandler {
    private final static String TIMEOUT_MESSAGE = "sorry, it's tooooo long processing, try again or later";
    private final static String EXCEPTION_MESSAGE = "something was wrong and your request has been interrupted, try again or later";
    @Value("${bot.handler.timeout}")
    private Integer timeout;

    @Override
    abstract public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event);

    /**
     * makes and sends {@link org.telegram.telegrambots.meta.api.objects.Message message} to the user
     *
     * @param chatId user's ID
     * @param answer answer to user
     * @return text message
     */
    protected SendMessage sendMessage(Long chatId, String answer) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(answer)
                .build();
    }

    /**
     * takes a picture from the url and sends it to the user
     *
     * @param url    link for the picture
     * @param chatId user's chatId
     */
    protected PartialBotApiMethod<?> sendImageFromUrl(String url, Long chatId) {
        // Create send method
        SendPhoto sendPhotoRequest = new SendPhoto();
        // Set destination chat id
        sendPhotoRequest.setChatId(chatId);
        // Set the photo url as a simple photo
        sendPhotoRequest.setPhoto(new InputFile(url));
        return sendPhotoRequest;
    }

    protected PartialBotApiMethod<?> editMessage(SendMessage message, String text) {
        new EditMessageText();
        return EditMessageText.builder()
                .chatId(message.getChatId())
                .messageId(message.getReplyToMessageId())
                .text(text)
                .replyMarkup((InlineKeyboardMarkup) message.getReplyMarkup())
                .build();
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
