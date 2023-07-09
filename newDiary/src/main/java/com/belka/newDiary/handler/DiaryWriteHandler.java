package com.belka.newDiary.handler;

import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.newDiary.service.DiaryService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@Data
@Component
public class DiaryWriteHandler implements BelkaHandler {

    private final static String CODE = "WRITE_DIARY";
    private final static String PREVIOUS_CODE = "/diary-buttons";
    private final static String ANSWER = "got it";
    private final PreviousService previousService;
    private final DiaryService diaryService;
    private final StatsService statsService;

    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        if (event.isHasText() && event.getPrevious_step().equals(PREVIOUS_CODE)) {
            Long chatId = event.getChatId();
            previousService.save(PreviousStepDto.builder()
                    .previousStep(CODE)
                    .userId(chatId)
                    .previousId(event.getUpdateId())
                    .build());
            diaryService.addNote(chatId, event.getText());
            statsService.save(StatsDto.builder()
                    .userId(event.getChatId())
                    .handlerCode(CODE)
                    .requestTime(LocalDateTime.now())
                    .build());
            return Flux.just(sendMessage(chatId));
        }
        return null;
    }

    private SendMessage sendMessage(Long chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(ANSWER)
                .build();
    }
}
