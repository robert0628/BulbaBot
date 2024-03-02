package com.belka.users.handler.subscribes.subscriptions.unsubscribe;

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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static com.belka.users.handler.subscribes.subscriptions.unsubscribe.ChooseWhoToUnsubscribeFromHandler.NO_BUTTON;
import static com.belka.users.handler.subscribes.subscriptions.unsubscribe.ChooseWhoToUnsubscribeFromHandler.YES_BUTTON;
import static com.belka.users.handler.subscribes.subscriptions.unsubscribe.UnsubscribeHandler.PREFIX_FOR_UNSUBSCRIBE_CALLBACK;

/**
 * delete chosen user from list of subscriptions
 */
@AllArgsConstructor
@Component
@Slf4j
public class DeleteSubscriptionHandler extends AbstractBelkaHandler {
    public final static String CODE = "/Choose Who To Unsubscribe From";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = ChooseWhoToUnsubscribeFromHandler.CODE;
    private final static String CLASS_NAME = DeleteSubscriptionHandler.class.getSimpleName();
    private final static String UNSUBSCRIBE_ANSWER = "unsubscribed";
    private final static String EVERYONE_STAYS_ANSWER = "Ok, everyone stays";
    private final UserService userService;
    private final ExecutorService executorService;
    private final StatsService statsService;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (isSubscribeCommandYes(event)) {
                Long chatId = event.getChatId();
                savePreviousStep(getPreviousStep(chatId), CLASS_NAME);
                recordStats(getStats(chatId));
                Long producerId = Long.valueOf(event.getData().substring((PREFIX_FOR_UNSUBSCRIBE_CALLBACK + YES_BUTTON).length()));
                userService.toUnsubscribe(chatId, producerId);
                SendMessage message = sendMessage(chatId, UNSUBSCRIBE_ANSWER);
                message.setReplyToMessageId(event.getUpdate().getCallbackQuery().getMessage().getMessageId());
                return Flux.just(editMessage(message, UNSUBSCRIBE_ANSWER));
            } else if (isSubscribeCommandNo(event)) {
                Long chatId = event.getChatId();
                savePreviousStep(getPreviousStep(chatId), CLASS_NAME);
                recordStats(getStats(chatId));
                SendMessage message = sendMessage(chatId, EVERYONE_STAYS_ANSWER);
                message.setReplyToMessageId(event.getUpdate().getCallbackQuery().getMessage().getMessageId());
                return Flux.just(editMessage(message, EVERYONE_STAYS_ANSWER));
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }

    private boolean isSubscribeCommandYes(BelkaEvent event) {
        return event.getPrevious_step().equals(PREVIOUS_HANDLER) && event.isHasCallbackQuery() &&
                event.getData().startsWith(PREFIX_FOR_UNSUBSCRIBE_CALLBACK + YES_BUTTON);
    }

    private boolean isSubscribeCommandNo(BelkaEvent event) {
        return event.getPrevious_step().equals(PREVIOUS_HANDLER) && event.isHasCallbackQuery() &&
                event.getData().startsWith(PREFIX_FOR_UNSUBSCRIBE_CALLBACK + NO_BUTTON);
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
