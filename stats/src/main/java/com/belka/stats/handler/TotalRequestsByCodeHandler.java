package com.belka.stats.handler;

import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class TotalRequestsByCodeHandler implements BelkaHandler {
    private final static String CODE = "Total Requests By Code Handler";
    private final static String PREVIOUS_CODE = "get stats";
    private final PreviousService previousService;
    private final StatsService statsService;

    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        if (event.isHasText() && event.getPrevious_step().equals(PREVIOUS_CODE)) {
            previousService.save(PreviousStepDto.builder()
                    .previousStep(CODE)
                    .userId(event.getChatId())
                    .previousId(event.getUpdateId())
                    .build());
            statsService.save(StatsDto.builder()
                    .userId(event.getChatId())
                    .handlerCode(CODE)
                    .requestTime(LocalDateTime.now())
                    .build());
            return Flux.just(sendMessage(event.getChatId(), String.valueOf(statsService.getTotalRequestsByCode(event.getText()))));
        }
        return null;
    }

    private SendMessage sendMessage(Long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }
}
