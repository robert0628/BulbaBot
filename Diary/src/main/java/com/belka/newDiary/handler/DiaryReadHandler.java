package com.belka.newDiary.handler;

import com.belka.core.BelkaSendMessage;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.newDiary.service.DiaryService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class DiaryReadHandler implements BelkaHandler {

    final static String CODE = "READ_DIARY";
    private final static String NEXT_HANDLER = DiaryCalendarHandler.CODE;
    private final static String PREVIOUS_HANDLER = DiaryStartHandler.CODE;
    private final DiaryService diaryService;
    private final PreviousService previousService;
    private final StatsService statsService;
    private final BelkaSendMessage belkaSendMessage;

    @Override
    @Transactional
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
                    .nextStep(NEXT_HANDLER)
                    .userId(event.getChatId())
                    .build());
            statsService.save(StatsDto.builder()
                    .userId(event.getChatId())
                    .handlerCode(CODE)
                    .requestTime(LocalDateTime.now())
                    .build());
            return Flux.just(belkaSendMessage.sendMessage(event.getChatId(), diaryService.getNote(date, event.getChatId())));
        }
        return null;
    }
}
