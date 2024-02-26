package com.belka.users.handler;


import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import com.belka.users.UserConfig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * the handler that starts the process of sending messages
 */
@Component
@AllArgsConstructor
public class PrepareToSendingMessagesHandler extends AbstractBelkaHandler {

    final static String CODE = "/send";
    private final static String NEXT_HANDLER = SendingMessageHandler.CODE;
    private final static String PREVIOUS_HANDLER = "";
    private final static String HEADER = "write some text";
    private final PreviousService previousService;
    private final UserConfig userConfig;
    private final StatsService statsService;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (isSubscribeCommand(event)) {
                Long chatId = event.getChatId();
                savePreviousStep(chatId);
                recordStats(chatId);
                return Flux.just(sendMessage(chatId, HEADER));
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }

    private boolean isSubscribeCommand(BelkaEvent event) {
        return event.isHasText()
                && event.getText().equalsIgnoreCase(CODE)
                && userConfig.getBotOwner().equals(event.getChatId());
    }

    private void savePreviousStep(Long chatId) {
        previousService.save(PreviousStepDto.builder()
                .previousStep(CODE)
                .nextStep(NEXT_HANDLER)
                .userId(chatId)
                .build());
    }

    private void recordStats(Long chatId) {
        statsService.save(StatsDto.builder()
                .userId(chatId)
                .handlerCode(CODE)
                .requestTime(OffsetDateTime.now())
                .build());
    }
}
