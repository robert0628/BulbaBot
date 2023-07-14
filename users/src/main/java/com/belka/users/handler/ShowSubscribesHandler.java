package com.belka.users.handler;

import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import com.belka.users.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * show user all his subscriptions
 */
@Component
@AllArgsConstructor
public class ShowSubscribesHandler implements BelkaHandler {
    private final static String CODE = "/subscribes";
    private final static String ANSWER_PREFIX = "You have %d subscription by now: %s";
    private final static String ANSWER_NO_SUBSCRIPTIONS = "You dont have any subscriptions";
    private final UserService userService;
    private final PreviousService previousService;
    private final StatsService statsService;

    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        if (event.isHasText() && event.getText().equalsIgnoreCase(CODE)) {
            Long chatId = event.getChatId();
            previousService.save(PreviousStepDto.builder()
                    .previousStep(CODE)
                    .userId(chatId)
                    .previousId(event.getUpdateId())
                    .build());
            statsService.save(StatsDto.builder()
                    .userId(chatId)
                    .handlerCode(CODE)
                    .requestTime(LocalDateTime.now())
                    .build());
            return Flux.just(sendMessage(chatId, getAnswer(chatId)));
        }
        return null;
    }

    private SendMessage sendMessage(Long chatId, String answer) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(answer)
                .build();
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
