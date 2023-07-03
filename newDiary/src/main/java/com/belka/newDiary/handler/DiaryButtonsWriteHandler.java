package com.belka.newDiary.handler;

import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import reactor.core.publisher.Flux;

import static com.belka.newDiary.handler.DiaryBaseHandler.WRITE_DIARY;

@Component
@AllArgsConstructor
public class DiaryButtonsWriteHandler implements BelkaHandler {
    private final static String CODE = "/diary-buttons";
    private final static String HEADER = "write some words";
    private final PreviousService previousService;

    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        if (event.getUpdate().hasCallbackQuery() && event.getData().equals(WRITE_DIARY)) {
            Long chatId = event.getChatId();
            previousService.save(PreviousStepDto.builder()
                    .previousStep(CODE)
                    .userId(chatId)
                    .build());
            return Flux.just(sendMessage(chatId));
        }
        return null;
    }

    private SendMessage sendMessage(Long chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(HEADER)
                .build();
    }
}
