package com.belka.QR.handler;

import com.belka.QR.Services.QRService;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.PreviousService;
import com.belka.core.previous_step.dto.PreviousStepDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@AllArgsConstructor
public class QRHandler implements BelkaHandler {
    private final static String CODE = "/QR";
    private final static String HEADER_1 = "write your text";
    private final PreviousService previousService;
    private final QRService qrService;

    @Override
    public PartialBotApiMethod<?> handle(BelkaEvent event) {
        Update update = event.getUpdate();
        if (update.hasMessage() && update.getMessage().hasText() && event.getMessage().equals(CODE)) {
            previousService.save(PreviousStepDto.builder()
                    .previousStep(CODE)
                    .userId(update.getMessage().getChatId())
                    .build());
            return sendMessage(update.getMessage().getChatId(), HEADER_1);
        }
        if (update.hasMessage() && update.getMessage().hasText() && event.getMessage().equals(CODE)) {
                Long chatId = event.getChatId();
                previousService.save(PreviousStepDto.builder()
                        .previousStep(CODE)
                        .userId(chatId)
                        .build());
                return sendImageFromUrl(qrService.getQRLink(event.getMessage()), chatId);
        }
        return null;
    }

    private SendMessage sendMessage(Long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }

    private PartialBotApiMethod<?> sendImageFromUrl(String url, Long chatId) {
        // Create send method
        SendPhoto sendPhotoRequest = new SendPhoto();
        // Set destination chat id
        sendPhotoRequest.setChatId(chatId);
        // Set the photo url as a simple photo
        sendPhotoRequest.setPhoto(new InputFile(url));
        return sendPhotoRequest;
        /*try {
            // Execute the method
            execute(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }*/
    }
}
