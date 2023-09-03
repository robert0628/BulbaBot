package com.belka.stats.handler;

import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * get total requests by code
 */
@Component
@AllArgsConstructor
public class TotalRequestsByCodeHandler extends AbstractBelkaHandler {
    private final static String CODE = "Total Requests By Code Handler";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = GetStatsHandler.CODE;
    private final PreviousService previousService;
    private final StatsService statsService;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (event.isHasText() && event.getPrevious_step().equals(PREVIOUS_HANDLER)) {
                previousService.save(PreviousStepDto.builder()
                        .previousStep(CODE)
                        .nextStep(NEXT_HANDLER)
                        .userId(event.getChatId())
                        .build());
                statsService.save(StatsDto.builder()
                        .userId(event.getChatId())
                        .handlerCode(CODE)
                        .requestTime(OffsetDateTime.now())
                        .build());
                return Flux.just(sendMessage(event.getChatId(), String.valueOf(statsService.getTotalRequestsByCode(event.getText()))));
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }
}
