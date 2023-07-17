package com.belka.stats.handler;

import com.belka.core.BelkaSendMessage;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.stats.StatsConfig;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

import static com.belka.stats.handler.StatsStartHandler.*;

/**
 * handle all the buttons selected to view the stats
 */
@Component
@AllArgsConstructor
@Setter
public class GetStatsHandler implements BelkaHandler {
    private final static String CODE = "get stats";
    private final static String answer = "write code";
    private final PreviousService previousService;
    private final StatsService statsService;
    private final StatsConfig statsConfig;
    private final BelkaSendMessage belkaSendMessage;

    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        if (event.getChatId().equals(statsConfig.getBotOwner()) && event.isHasCallbackQuery()) {
            switch (event.getData()) {
                case BUTTON_1 -> {
                    savePreviousAndStats(event);
                    return Flux.just(belkaSendMessage.sendMessage(event.getChatId(), String.valueOf(statsService.getTotalRequests())));
                }
                case BUTTON_2 -> {
                    savePreviousAndStats(event);
                    return Flux.just(belkaSendMessage.sendMessage(event.getChatId(), String.valueOf(statsService.getTotalRequestsByUser(event.getChatId()))));
                }
                case BUTTON_3 -> {
                    savePreviousAndStats(event);
                    return Flux.just(belkaSendMessage.sendMessage(event.getChatId(), answer));
                }
                case BUTTON_4 -> {
                    savePreviousAndStats(event);
                    return Flux.just(belkaSendMessage.sendMessage(event.getChatId(), String.valueOf(statsService.getMostPopularRequest())));
                }
                case BUTTON_5 -> {
                    savePreviousAndStats(event);
                    return Flux.just(belkaSendMessage.sendMessage(event.getChatId(), String.valueOf(statsService.getMostPopularRequestByUser(event.getChatId()))));
                }
            }
        }
        return null;
    }

    private void savePreviousAndStats(BelkaEvent event) {
        previousService.save(PreviousStepDto.builder()
                .previousStep(CODE)
                .userId(event.getChatId())
                .build());
        statsService.save(StatsDto.builder()
                .userId(event.getChatId())
                .handlerCode(CODE)
                .requestTime(LocalDateTime.now())
                .build());
    }
}
