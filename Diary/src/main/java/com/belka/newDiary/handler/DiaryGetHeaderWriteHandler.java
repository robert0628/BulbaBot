package com.belka.newDiary.handler;

import com.belka.core.BelkaSendMessage;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.handlers.BelkaHandler;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

import static com.belka.newDiary.handler.DiaryStartHandler.BUTTON_2;

@Component
@AllArgsConstructor
public class DiaryGetHeaderWriteHandler implements BelkaHandler {
    final static String CODE = "write diary header";
    private final static String NEXT_HANDLER = DiaryWriteHandler.CODE;
    private final static String PREVIOUS_HANDLER = DiaryStartHandler.CODE;
    private final static String HEADER = "write some words";
    private final static String PREVIOUS_DATA = DiaryStartHandler.CODE + BUTTON_2;
    private final PreviousService previousService;
    private final StatsService statsService;
    private final BelkaSendMessage belkaSendMessage;

    @Override
    @Transactional
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        if (event.getUpdate().hasCallbackQuery() && event.getData().equals(PREVIOUS_DATA)) {
            Long chatId = event.getChatId();
            previousService.save(PreviousStepDto.builder()
                    .previousStep(CODE)
                    .nextStep(NEXT_HANDLER)
                    .userId(chatId)
                    .build());
            statsService.save(StatsDto.builder()
                    .userId(event.getChatId())
                    .handlerCode(CODE)
                    .requestTime(LocalDateTime.now())
                    .build());
            return Flux.just(belkaSendMessage.sendMessage(chatId, HEADER));
        }
        return null;
    }
}
