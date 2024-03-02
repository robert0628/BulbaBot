package com.belka.stats.handler;

import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * get total requests by code
 */
@Component
@AllArgsConstructor
@Slf4j
public class TotalRequestsByCodeHandler extends AbstractBelkaHandler {
    private final static String CODE = "Total Requests By Code Handler";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = GetStatsHandler.CODE;
    private final static String CLASS_NAME = TotalRequestsByCodeHandler.class.getSimpleName();

    private final ExecutorService executorService;
    private final StatsService statsService;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (isSubscribeCommand(event)) {
                Long chatId = event.getChatId();
                savePreviousStep(getPreviousStep(chatId), CLASS_NAME);
                recordStats(getStats(chatId));
                return Flux.just(sendMessage(event.getChatId(), String.valueOf(statsService.getTotalRequestsByCode(event.getText()))));
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }

    private boolean isSubscribeCommand(BelkaEvent event) {
        return event.isHasText() && event.getPrevious_step().equals(PREVIOUS_HANDLER);
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
