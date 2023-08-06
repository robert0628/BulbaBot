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
import org.telegram.telegrambots.meta.api.objects.Contact;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Component
@AllArgsConstructor
public class IncomingContactHandler extends AbstractBelkaHandler {
    final static String CODE = "Subscription";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = SubscribeHandler.CODE;
    private final static String SUCCESSFULLY_ANSWER = "subscription is issued";
    private final static String FAILED_ANSWER = "this user is not registered";
    private final PreviousService previousService;
    private final StatsService statsService;
    private final UserService userService;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (event.getPrevious_step().equals(PREVIOUS_HANDLER) && event.isHasMessage()
                    && event.getUpdate().getMessage().hasContact()) {

                Contact contact = event.getUpdate().getMessage().getContact();
                if (userService.existsById(contact.getUserId())) {
                    userService.toSubscribe(event.getChatId(), contact.getUserId());
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

                    return Flux.just(sendMessage(chatId, SUCCESSFULLY_ANSWER));
                }
                return Flux.just(sendMessage(event.getChatId(), FAILED_ANSWER));
            }
            return Flux.empty();
        });
        return future(future, event.getChatId());
    }
}
