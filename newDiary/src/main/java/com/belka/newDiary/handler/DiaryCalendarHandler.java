package com.belka.newDiary.handler;

import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.PreviousService;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.newDiary.service.CalendarService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

import static com.belka.newDiary.handler.DiaryBaseHandler.GET_DIARY;

@Component
@AllArgsConstructor
public class DiaryCalendarHandler implements BelkaHandler {

    private final static String CODE = "READ_DIARY";
    private final static String PREVIOUS = "PREV-MONTH";
    private final static String NEXT = "NEXT-MONTH";
    private final PreviousService previousService;
    private final CalendarService calendarService;


    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        if (event.getUpdate().hasCallbackQuery() && event.getData().equals(GET_DIARY)) {
            Long chatId = event.getChatId();
            LocalDate date = LocalDate.now();
            Integer YEAR = date.getYear();
            Integer MONTH = date.getMonth().getValue()-1;
            previousService.save(PreviousStepDto.builder()
                    .previousStep(CODE)
                    .userId(chatId)
                    .build());
            return Flux.just(calendarService.sendCalendarMessage(chatId, YEAR, MONTH));
        } else if (event.getUpdate().hasCallbackQuery() && (event.getData().startsWith(PREVIOUS) || event.getData().startsWith(NEXT))) {
            String dateString = event.getData().substring(11);
            String[] dateArray = dateString.split("-");
            Integer YEAR = Integer.parseInt(dateArray[0]);
            Integer MONTH = Integer.parseInt(dateArray[1]);
            Long chatId = event.getChatId();
            previousService.save(PreviousStepDto.builder()
                    .previousStep(CODE)
                    .userId(chatId)
                    .build());
            return Flux.just(calendarService.sendCalendarMessage(chatId, YEAR, MONTH));
        }

        return null;
    }

}
