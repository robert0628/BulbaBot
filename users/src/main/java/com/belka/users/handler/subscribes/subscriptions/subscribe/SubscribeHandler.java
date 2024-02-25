package com.belka.users.handler.subscribes.subscriptions.subscribe;

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
 * a handler that handles the request when the user wants to subscribe to someone new
 */
@Component
@AllArgsConstructor
public class SubscribeHandler extends AbstractBelkaHandler {
    public final static String CODE = "/subscribe";
    private final static String NEXT_HANDLER = IncomingContactHandler.CODE;
    private final static String PREVIOUS_HANDLER = "";
    private final static String ANSWER = "share the contact here";
    private final PreviousService previousService;
    private final StatsService statsService;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (isSubscribeCommand(event)) {
                Long chatId = event.getChatId();
                savePreviousStep(chatId);
                recordStats(chatId);
                return Flux.just(sendMessage(event.getChatId(), ANSWER));
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }

    private boolean isSubscribeCommand(BelkaEvent event) {
        return event.isHasText() && event.getText().equalsIgnoreCase(CODE) ||
                event.isHasCallbackQuery() && event.getData().equals(CODE);
    }

    private void savePreviousStep(Long chatId) {
        PreviousStepDto previousStepDto = PreviousStepDto.builder()
                .previousStep(CODE)
                .nextStep(NEXT_HANDLER)
                .userId(chatId)
                .build();
        previousService.save(previousStepDto);
    }

    private void recordStats(Long chatId) {
        StatsDto statsDto = StatsDto.builder()
                .userId(chatId)
                .handlerCode(CODE)
                .requestTime(OffsetDateTime.now())
                .build();
        statsService.save(statsDto);
    }
}
