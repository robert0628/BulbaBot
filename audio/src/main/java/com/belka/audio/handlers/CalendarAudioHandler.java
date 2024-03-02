package com.belka.audio.handlers;

import com.belka.audio.services.AudioCalendarService;
import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Component
@AllArgsConstructor
@Slf4j
public class CalendarAudioHandler extends AbstractBelkaHandler {
    final static String CODE = "/audio calendar";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = "";
    private final static String CLASS_NAME = CalendarAudioHandler.class.getSimpleName();
    private final static String HEADER = "Calendar";
    private final AudioCalendarService calendarService;
    private final ExecutorService executorService;
    private final StatsService statsService;

    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (event.isHasCallbackQuery() && event.getData().equals(CODE)) {
                Long chatId = event.getChatId();
                LocalDate date = LocalDate.now();
                Integer YEAR = date.getYear();
                Integer MONTH = date.getMonth().getValue() - 1;
                savePreviousStep(getPreviousStep(chatId), CLASS_NAME);
                recordStats(getStats(chatId));
                SendMessage message = calendarService.sendCalendarMessage(chatId, YEAR, MONTH);
                message.setReplyToMessageId(event.getUpdate().getCallbackQuery().getMessage().getMessageId());

                return Flux.just(editMessage(message, HEADER));
            } else if (event.getUpdate().hasCallbackQuery() &&
                    (event.getData().startsWith(AudioCalendarService.AUDIO_CALENDAR_NEXT_MONTH) ||
                            event.getData().startsWith(AudioCalendarService.AUDIO_CALENDAR_PREV_MONTH))) {
                String[] dateArray = event.getData().split("-");
                Integer YEAR = Integer.parseInt(dateArray[1]);
                Integer MONTH = Integer.parseInt(dateArray[2]);
                Long chatId = event.getChatId();
                SendMessage message = calendarService.sendCalendarMessage(chatId, YEAR, MONTH);
                message.setReplyToMessageId(event.getUpdate().getCallbackQuery().getMessage().getMessageId());
                savePreviousStep(getPreviousStep(chatId), CLASS_NAME);
                recordStats(getStats(chatId));
                return Flux.just(editMessage(message, HEADER));
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }

    private PreviousStepDto getPreviousStep(Long chatId) {
        return PreviousStepDto.builder()
                .previousStep(CODE)
                .nextStep(NEXT_HANDLER)
                .userId(chatId)
                .data("")
                .build();
    }

    private StatsDto getStats(Long chatId) {
        return StatsDto.builder()
                .userId(chatId)
                .handlerCode(CODE)
                .requestTime(OffsetDateTime.now())
                .build();
    }

    private void recordStats(StatsDto statsDto) {
        executorService.execute(() -> {
                    statsService.save(statsDto);
                    log.info("Stats from {} have been recorded", CLASS_NAME);
                }
        );
    }
}
