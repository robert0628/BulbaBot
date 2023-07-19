package com.belka.stats.handler;

import com.belka.core.BelkaSendMessage;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

/**
 * get total requests by code
 */
@Component
@AllArgsConstructor
public class TotalRequestsByCodeHandler implements BelkaHandler {
    private final static String CODE = "Total Requests By Code Handler";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = GetStatsHandler.CODE;
    private final PreviousService previousService;
    private final StatsService statsService;
    private final BelkaSendMessage belkaSendMessage;

    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        if (event.isHasText() && event.getPrevious_step().equals(PREVIOUS_HANDLER)) {
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
            return Flux.just(belkaSendMessage.sendMessage(event.getChatId(), String.valueOf(statsService.getTotalRequestsByCode(event.getText()))));
        }
        return null;
    }
}
