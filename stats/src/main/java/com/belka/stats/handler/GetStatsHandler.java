package com.belka.stats.handler;

import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.stats.StatsConfig;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static com.belka.stats.handler.StatsStartHandler.*;

/**
 * handle all the buttons selected to view the stats
 */
@Component
@AllArgsConstructor
@Setter
public class GetStatsHandler extends AbstractBelkaHandler {
    final static String CODE = "get stats";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = StatsStartHandler.CODE;
    private final static String ANSWER = "write code";
    private final PreviousService previousService;
    private final StatsService statsService;
    private final StatsConfig statsConfig;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (event.getChatId().equals(statsConfig.getBotOwner()) && event.isHasCallbackQuery()) {
                switch (event.getData()) {
                    case BUTTON_1 -> {
                        savePreviousAndStats(event);
                        return Flux.just(sendMessage(event.getChatId(), String.valueOf(statsService.getTotalRequests())));
                    }
                    case BUTTON_2 -> {
                        savePreviousAndStats(event);
                        return Flux.just(sendMessage(event.getChatId(), String.valueOf(statsService.getTotalRequestsByUser(event.getChatId()))));
                    }
                    case BUTTON_3 -> {
                        savePreviousAndStats(event);
                        return Flux.just(sendMessage(event.getChatId(), ANSWER));
                    }
                    case BUTTON_4 -> {
                        savePreviousAndStats(event);
                        return Flux.just(sendMessage(event.getChatId(), String.valueOf(statsService.getMostPopularRequest())));
                    }
                    case BUTTON_5 -> {
                        savePreviousAndStats(event);
                        return Flux.just(sendMessage(event.getChatId(), String.valueOf(statsService.getMostPopularRequestByUser(event.getChatId()))));
                    }
                }
            }
            return Flux.empty();
        });
        return future(future, event.getChatId());
    }

    private void savePreviousAndStats(BelkaEvent event) {
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
    }
}
