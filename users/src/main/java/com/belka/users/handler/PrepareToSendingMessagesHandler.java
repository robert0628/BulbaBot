package com.belka.users.handler;


import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import com.belka.users.UserConfig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

/**
 * the handler that starts the process of sending messages
 */
@Component
@AllArgsConstructor
public class PrepareToSendingMessagesHandler implements BelkaHandler {

    private final static String CODE = "/send";
    private final static String HEADER = "write some text";
    private final PreviousService previousService;
    private final UserConfig userConfig;
    private final StatsService statsService;

    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        if (event.isHasText()
                && event.getText().equalsIgnoreCase(CODE)
                && userConfig.getBotOwner().equals(event.getChatId())) {

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

            return Flux.just(sendMessage(chatId));
        }
        return null;
    }

    private SendMessage sendMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(HEADER);
        return message;
    }
}
