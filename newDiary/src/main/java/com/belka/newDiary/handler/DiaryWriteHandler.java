package com.belka.newDiary.handler;

import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.PreviousService;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.newDiary.service.DiaryService;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Data
@Component
public class DiaryWriteHandler implements BelkaHandler {

    private final static String CODE = "WRITE_DIARY";
    private final static String PREVIOUS_CODE = "/diary-buttons";
    private final PreviousService previousService;
    private final DiaryService diaryService;

    @Override
    public PartialBotApiMethod<?> handle(BelkaEvent event) {
        Update update = event.getUpdate();
        if (update.hasMessage() &&
                update.getMessage().hasText() &&
                event.getPrevious_step().equals(PREVIOUS_CODE)) {
            Long chatId = event.getChatId();
            previousService.save(PreviousStepDto.builder()
                    .previousStep(CODE)
                    .userId(chatId)
                    .build());
            diaryService.addNote(chatId, event.getMessage());
            return sendMessage(chatId);
        }
        return null;
    }

    private SendMessage sendMessage(Long chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .text("got it")
                .build();
    }
}
