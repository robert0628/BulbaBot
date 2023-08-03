package com.belka.core.handlers;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import reactor.core.publisher.Flux;

public abstract class AbstractBelkaHandler implements BelkaHandler{
    @Override
    abstract public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event);

    /**
     * makes and sends {@link org.telegram.telegrambots.meta.api.objects.Message message} to the user
     *
     * @param chatId user's ID
     * @param answer answer to user
     * @return text message
     */
    public SendMessage sendMessage(Long chatId, String answer) {
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
    public PartialBotApiMethod<?> sendImageFromUrl(String url, Long chatId) {
        // Create send method
        SendPhoto sendPhotoRequest = new SendPhoto();
        // Set destination chat id
        sendPhotoRequest.setChatId(chatId);
        // Set the photo url as a simple photo
        sendPhotoRequest.setPhoto(new InputFile(url));
        return sendPhotoRequest;
    }

    public PartialBotApiMethod<?> editMessage(SendMessage message, String text) {
        new EditMessageText();
        return EditMessageText.builder()
                .chatId(message.getChatId())
                .messageId(message.getReplyToMessageId())
                .text(text)
                .replyMarkup((InlineKeyboardMarkup) message.getReplyMarkup())
                .build();
    }
}
