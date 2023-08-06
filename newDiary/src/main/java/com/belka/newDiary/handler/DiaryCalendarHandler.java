package com.belka.newDiary.handler;

import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.newDiary.service.CalendarService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Component
@AllArgsConstructor
public class DiaryCalendarHandler extends AbstractBelkaHandler {
    final static String CODE = "CALENDAR_DIARY";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = DiaryReadHandler.CODE;
    private final static String PREVIOUS = "PREV-MONTH";
    private final static String NEXT = "NEXT-MONTH";
    private final static String HEADER = "Calendar";
    private final static String PREVIOUS_DATA = DiaryStartHandler.CODE + DiaryStartHandler.BUTTON_1;
    private final PreviousService previousService;
    private final CalendarService calendarService;
    private final StatsService statsService;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (event.isHasCallbackQuery() && event.getData().equals(PREVIOUS_DATA)) {
                Long chatId = event.getChatId();
                LocalDate date = LocalDate.now();
                Integer YEAR = date.getYear();
                Integer MONTH = date.getMonth().getValue() - 1;
                previousService.save(PreviousStepDto.builder()
                        .previousStep(CODE)
                        .userId(chatId)
                        .nextStep(NEXT_HANDLER)
                        .build());
                statsService.save(StatsDto.builder()
                        .userId(event.getChatId())
                        .handlerCode(CODE)
                        .requestTime(LocalDateTime.now())
                        .build());
                return Flux.just(calendarService.sendCalendarMessage(chatId, YEAR, MONTH));
            } else if (event.getUpdate().hasCallbackQuery() && (event.getData().startsWith(PREVIOUS) || event.getData().startsWith(NEXT))) {
                String dateString = event.getData().substring(11);
                String[] dateArray = dateString.split("-");
                Integer YEAR = Integer.parseInt(dateArray[0]);
                Integer MONTH = Integer.parseInt(dateArray[1]);
                Long chatId = event.getChatId();
                SendMessage message = calendarService.sendCalendarMessage(chatId, YEAR, MONTH);
                message.setReplyToMessageId(event.getUpdate().getCallbackQuery().getMessage().getMessageId());
                previousService.save(PreviousStepDto.builder()
                        .previousStep(CODE)
                        .userId(chatId)
                        .build());
                statsService.save(StatsDto.builder()
                        .userId(event.getChatId())
                        .handlerCode(CODE)
                        .requestTime(LocalDateTime.now())
                        .build());
                return Flux.just(editMessage(message, HEADER));
            }

            return Flux.empty();
        });
        return future(future, event.getChatId());
    }
}
