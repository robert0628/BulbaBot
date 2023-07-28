package com.belka.users.handler;


import com.belka.core.BelkaSendMessage;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
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

import java.time.LocalDateTime;

/**
 * the handler that starts the process of sending messages
 */
@Component
@AllArgsConstructor
public class PrepareToSendingMessagesHandler implements BelkaHandler {

    final static String CODE = "/send";
    private final static String NEXT_HANDLER = SendingMessageHandler.CODE;
    private final static String PREVIOUS_HANDLER = "";
    private final static String HEADER = "write some text";
    private final PreviousService previousService;
    private final UserConfig userConfig;
    private final StatsService statsService;
    private final BelkaSendMessage belkaSendMessage;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        if (event.isHasText()
                && event.getText().equalsIgnoreCase(CODE)
                && userConfig.getBotOwner().equals(event.getChatId())) {

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

            return Flux.just(belkaSendMessage.sendMessage(chatId, HEADER));
        }
        return null;
    }
}
