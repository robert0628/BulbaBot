package com.belka.users.handler.subscribes.subscibers;

import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import com.belka.users.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * show user all his subscribers
 */
@Component
@AllArgsConstructor
@Slf4j
public class GetSubscribersHandler extends AbstractBelkaHandler {
    public final static String CODE = "/get_subscribers";
    private final static String NEXT_HANDLER = SubscribersHandler.CODE;
    private final static String PREVIOUS_HANDLER = "";
    private final static String CLASS_NAME = GetSubscribersHandler.class.getSimpleName();
    private final static String ANSWER_PREFIX = "You have %d subscribers by now: %s";
    private final static String ANSWER_NO_SUBSCRIPTIONS = "You don't have any subscribers";
    private final UserService userService;
    private final StatsService statsService;
    private final ExecutorService executorService;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (isSubscribeCommand(event, CODE)) {
                Long chatId = event.getChatId();
                savePreviousStep(getPreviousStep(chatId), CLASS_NAME);
                recordStats(getStats(chatId));
                return Flux.just(sendMessage(chatId, getAnswer(chatId)));
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }

    private String getAnswer(Long chatId) {
        Collection<String> subscribes = userService.getFollowersNames(chatId);
        if (subscribes.isEmpty()) {
            return ANSWER_NO_SUBSCRIPTIONS;
        }
        String names = subscribes.toString().substring(1, subscribes.toString().length() - 1);
        return String.format(ANSWER_PREFIX, subscribes.size(), names);
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
