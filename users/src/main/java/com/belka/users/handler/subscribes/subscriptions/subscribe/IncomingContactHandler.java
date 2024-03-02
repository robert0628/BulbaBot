package com.belka.users.handler.subscribes.subscriptions.subscribe;

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
import org.telegram.telegrambots.meta.api.objects.Contact;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Component
@AllArgsConstructor
@Slf4j
public class IncomingContactHandler extends AbstractBelkaHandler {
    public final static String CODE = "Incoming Contact";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = SubscribeHandler.CODE;
    private final static String CLASS_NAME = IncomingContactHandler.class.getSimpleName();
    private final static String SUCCESSFULLY_ANSWER = "subscription is issued";
    private final static String FAILED_ANSWER = "this user is not registered";
    private final ExecutorService executorService;
    private final StatsService statsService;
    private final UserService userService;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (isSubscribeCommand(event)) {
                Contact contact = event.getUpdate().getMessage().getContact();
                if (userService.existsById(contact.getUserId())) {
                    userService.toSubscribe(event.getChatId(), contact.getUserId());
                    Long chatId = event.getChatId();
                    savePreviousStep(getPreviousStep(chatId), CLASS_NAME);
                    recordStats(getStats(chatId));

                    return Flux.just(sendMessage(chatId, SUCCESSFULLY_ANSWER));
                }
                return Flux.just(sendMessage(event.getChatId(), FAILED_ANSWER));
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }

    private boolean isSubscribeCommand(BelkaEvent event) {
        return event.isHasMessage() && event.getUpdate().getMessage().hasContact();
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
