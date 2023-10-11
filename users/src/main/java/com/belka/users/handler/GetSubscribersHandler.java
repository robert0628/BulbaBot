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

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * show user all his subscribers
 */
@Component
@AllArgsConstructor
public class GetSubscribersHandler extends AbstractBelkaHandler {
    public final static String CODE = "/get_subscribers";
    private final static String NEXT_HANDLER = SubscribersHandler.CODE;
    private final static String PREVIOUS_HANDLER = "";
    private final static String ANSWER_PREFIX = "You have %d subscribers by now: %s";
    private final static String ANSWER_NO_SUBSCRIPTIONS = "You don't have any subscribers";
    private final UserService userService;
    private final PreviousService previousService;
    private final StatsService statsService;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (event.isHasText() && event.getText().equalsIgnoreCase(CODE) ||
                    event.isHasCallbackQuery() && event.getData().equals(CODE)) {
                Long chatId = event.getChatId();
                previousService.save(PreviousStepDto.builder()
                        .previousStep(CODE)
                        .nextStep(NEXT_HANDLER)
                        .userId(chatId)
                        .build());
                statsService.save(StatsDto.builder()
                        .userId(chatId)
                        .handlerCode(CODE)
                        .requestTime(OffsetDateTime.now())
                        .build());
                return Flux.just(sendMessage(chatId, getAnswer(chatId)));
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }

    private String getAnswer(Long chatId) {
        Collection<String> subscribes = userService.getFollowers(chatId);
        if (subscribes.isEmpty()) {
            return ANSWER_NO_SUBSCRIPTIONS;
        }
        String names = subscribes.toString().substring(1, subscribes.toString().length() - 1);
        return String.format(ANSWER_PREFIX, subscribes.size(), names);
    }
}
