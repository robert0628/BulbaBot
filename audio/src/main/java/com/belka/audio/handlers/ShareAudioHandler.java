package com.belka.audio.handlers;

import com.belka.audio.services.AudioService;
import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Component
@AllArgsConstructor
public class ShareAudioHandler extends AbstractBelkaHandler {
    final static String CODE = "share audio handler";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = SaveAudioHandler.CODE;
    private final AudioService audioService;
    private final PreviousService previousService;
    private final StatsService statsService;

    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (event.getPrevious_step().equals(PREVIOUS_HANDLER)) {
                Long chatId = event.getChatId();
                if (event.getData().equals(SaveAudioHandler.BUTTON_SHARE)) {
                    audioService.changeIsPrivateFlag(true, event.getData());
                    previousService.save(PreviousStepDto.builder()
                            .previousStep(CODE)
                            .nextStep(NEXT_HANDLER)
                            .userId(chatId)
                            .data("")
                            .build());
                    statsService.save(StatsDto.builder()
                            .userId(event.getChatId())
                            .handlerCode(CODE)
                            .requestTime(LocalDateTime.now())
                            .build());
                    return Flux.just(sendMessage(chatId, "Great, the world will hear your voice"));
                } else {
                    previousService.save(PreviousStepDto.builder()
                            .previousStep(CODE)
                            .nextStep(NEXT_HANDLER)
                            .userId(chatId)
                            .data("")
                            .build());
                    statsService.save(StatsDto.builder()
                            .userId(event.getChatId())
                            .handlerCode(CODE)
                            .requestTime(LocalDateTime.now())
                            .build());
                    return Flux.just(sendMessage(chatId, "OK, the message will remain private"));
                }
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }
}
