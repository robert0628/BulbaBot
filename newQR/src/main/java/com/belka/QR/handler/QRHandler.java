package com.belka.QR.handler;

import com.belka.QR.Services.QRService;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.stats.StatsDto;
import com.belka.stats.StatsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

/**
 * the handler that processes the user's request to create a QR code
 */
@Component
@AllArgsConstructor
public class QRHandler implements BelkaHandler {
    private final static String CODE = "/QR";
    private final static String EXIT_CODE = "/send QR";
    private final static String HEADER_1 = "write your text";
    private final PreviousService previousService;
    private final QRService qrService;
    private final StatsService statsService;

    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        if (event.isHasMessage() && event.isHasText() && event.getText().equalsIgnoreCase(CODE)) {
            previousService.save(PreviousStepDto.builder()
                    .previousStep(CODE)
                    .userId(event.getChatId())
                    .previousId(event.getUpdateId())
                    .build());
            statsService.save(StatsDto.builder()
                    .userId(event.getChatId())
                    .handlerCode(CODE)
                    .requestTime(LocalDateTime.now())
                    .build());
            return Flux.just(sendMessage(event.getChatId()));
        }
        if (event.isHasMessage() && event.isHasText() && event.getPrevious_step().equals(CODE)) {
            Long chatId = event.getChatId();
            previousService.save(PreviousStepDto.builder()
                    .previousStep(EXIT_CODE)
                    .userId(chatId)
                    .previousId(event.getUpdateId())
                    .build());
            statsService.save(StatsDto.builder()
                    .userId(event.getChatId())
                    .handlerCode(CODE)
                    .requestTime(LocalDateTime.now())
                    .build());
            return Flux.just(sendImageFromUrl(qrService.getQRLink(event.getText()), chatId));
        }
        return null;
    }

    private SendMessage sendMessage(Long chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(HEADER_1)
                .build();
    }

    /**
     * takes a picture from the url and sends it to the user
     *
     * @param url    link for the picture
     * @param chatId user's chatId
     */
    private PartialBotApiMethod<?> sendImageFromUrl(String url, Long chatId) {
        // Create send method
        SendPhoto sendPhotoRequest = new SendPhoto();
        // Set destination chat id
        sendPhotoRequest.setChatId(chatId);
        // Set the photo url as a simple photo
        sendPhotoRequest.setPhoto(new InputFile(url));
        return sendPhotoRequest;
    }
}
