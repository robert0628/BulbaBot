package com.belka.users.handler;

import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import com.belka.users.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * show user all his subscriptions
 */
@Component
@AllArgsConstructor
public class ShowSubscribesHandler extends AbstractBelkaHandler {
    private final static String CODE = "/subscribes";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = "";
    private final static String ANSWER_PREFIX = "You have %d subscription by now: %s";
    private final static String ANSWER_NO_SUBSCRIPTIONS = "You dont have any subscriptions";
    private final UserService userService;
    private final PreviousService previousService;
    private final StatsService statsService;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (event.isHasText() && event.getText().equalsIgnoreCase(CODE)) {
                Long chatId = event.getChatId();
                previousService.save(PreviousStepDto.builder()
                        .previousStep(CODE)
                        .nextStep(NEXT_HANDLER)
                        .userId(chatId)
                        .build());
                statsService.save(StatsDto.builder()
                        .userId(chatId)
                        .handlerCode(CODE)
                        .requestTime(LocalDateTime.now())
                        .build());
                return Flux.just(sendMessage(chatId, getAnswer(chatId)));
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }

    private String getAnswer(Long chatId) {
        Collection<String> subscribes = userService.showSubscribes(chatId);
        if (subscribes.isEmpty()) {
            return ANSWER_NO_SUBSCRIPTIONS;
        }
        String names = subscribes.toString().substring(1, subscribes.toString().length() - 1);
        return String.format(ANSWER_PREFIX, subscribes.size(), names);
    }
}
