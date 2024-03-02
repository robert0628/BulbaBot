package com.belka.stats.handler;

import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.stats.StatsConfig;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static com.belka.stats.handler.StatsStartHandler.*;

/**
 * handle all the buttons selected to view the stats
 */
@Component
@AllArgsConstructor
@Setter
@Slf4j
public class GetStatsHandler extends AbstractBelkaHandler {
    final static String CODE = "get stats";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = StatsStartHandler.CODE;
    private final static String CLASS_NAME = GetStatsHandler.class.getSimpleName();
    private final static String ANSWER = "what code?";
    private final ExecutorService executorService;
    private final StatsService statsService;
    private final StatsConfig statsConfig;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (event.getChatId().equals(statsConfig.getBotOwner()) && event.isHasCallbackQuery()) {
                Long chatId = event.getChatId();
                switch (event.getData()) {
                    case BUTTON_1 -> {
                        savePreviousAndStats(chatId);
                        return Flux.just(sendMessage(chatId, String.valueOf(statsService.getTotalRequests())));
                    }
                    case BUTTON_2 -> {
                        savePreviousAndStats(chatId);
                        return Flux.just(sendMessage(chatId, String.valueOf(statsService.getTotalRequestsByUser(event.getChatId()))));
                    }
                    case BUTTON_3 -> {
                        savePreviousAndStats(chatId);
                        return Flux.just(sendMessage(chatId, ANSWER));
                    }
                    case BUTTON_4 -> {
                        savePreviousAndStats(chatId);
                        return Flux.just(sendMessage(chatId, String.valueOf(statsService.getMostPopularRequest())));
                    }
                    case BUTTON_5 -> {
                        savePreviousAndStats(chatId);
                        return Flux.just(sendMessage(chatId, String.valueOf(statsService.getMostPopularRequestByUser(chatId))));
                    }
                }
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }

    private void savePreviousAndStats(Long chatId) {
        savePreviousStep(getPreviousStep(chatId), CLASS_NAME);
        recordStats(getStats(chatId));
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
