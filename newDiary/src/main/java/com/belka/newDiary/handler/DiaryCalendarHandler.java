package com.belka.newDiary.handler;

import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.newDiary.service.DiaryCalendarService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

@Component
@AllArgsConstructor
public class DiaryCalendarHandler extends AbstractBelkaHandler {
    final static String CODE = "CALENDAR_DIARY";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = DiaryReadHandler.CODE;
    private final static String HEADER = "Calendar";
    private final static String PREVIOUS_DATA = DiaryStartHandler.CODE + DiaryStartHandler.BUTTON_1;
    private final PreviousService previousService;
    private final DiaryCalendarService diaryCalendarService;
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
                        .requestTime(OffsetDateTime.now())
                        .build());
                return Flux.just(diaryCalendarService.sendCalendarMessage(chatId, YEAR, MONTH));
            } else if (event.getUpdate().hasCallbackQuery() &&
                    (event.getData().startsWith(DiaryCalendarService.DIARY_CALENDAR_PREV_MONTH) ||
                            event.getData().startsWith(DiaryCalendarService.DIARY_CALENDAR_NEXT_MONTH))) {
                String[] dateArray = event.getData().split("-");
                Integer YEAR = Integer.parseInt(dateArray[1]);
                Integer MONTH = Integer.parseInt(dateArray[2]);
                Long chatId = event.getChatId();
                SendMessage message = diaryCalendarService.sendCalendarMessage(chatId, YEAR, MONTH);
                message.setReplyToMessageId(event.getUpdate().getCallbackQuery().getMessage().getMessageId());
                previousService.save(PreviousStepDto.builder()
                        .previousStep(CODE)
                        .userId(chatId)
                        .build());
                statsService.save(StatsDto.builder()
                        .userId(chatId)
                        .handlerCode(CODE)
                        .requestTime(OffsetDateTime.now())
                        .build());
                return Flux.just(editMessage(message, HEADER));
            }

            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }
}
