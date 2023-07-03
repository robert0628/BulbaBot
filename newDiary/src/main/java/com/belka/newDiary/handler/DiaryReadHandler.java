package com.belka.newDiary.handler;

import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.newDiary.service.DiaryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Component
@AllArgsConstructor
public class DiaryReadHandler implements BelkaHandler {

    private final static String CODE = "DIARY_WRITE";
    private final DiaryService diaryService;
    private final PreviousService previousService;

    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        if (event.isHasCallbackQuery() && event.getData().startsWith("DAY-")) {
            String dateString = event.getData().substring(4);
            String[] dateArray = dateString.split("\\.");
            Integer YEAR = Integer.parseInt(dateArray[0]);
            Integer MONTH = Integer.parseInt(dateArray[1]);
            Integer DAY = Integer.parseInt(dateArray[2]);
            LocalDate date = LocalDate.of(YEAR, MONTH, DAY);

            previousService.save(PreviousStepDto.builder()
                    .previousStep(CODE)
                    .userId(event.getChatId())
                    .build());

            return Flux.just(sendMessage(event.getChatId(), diaryService.getNote(date, event.getChatId())));
        }
        return null;
    }

    private SendMessage sendMessage(Long chatId, String note) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(note)
                .build();
    }
}
