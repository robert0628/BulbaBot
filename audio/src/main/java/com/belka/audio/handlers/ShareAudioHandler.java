package com.belka.audio.handlers;

import com.belka.audio.services.AudioService;
import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import com.belka.core.previous_step.dto.PreviousStepDto;
import com.belka.core.previous_step.service.PreviousService;
import com.belka.stats.StatsDto;
import com.belka.stats.service.StatsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Component
@AllArgsConstructor
@Slf4j
public class ShareAudioHandler extends AbstractBelkaHandler {
    final static String CODE = "share audio handler";
    private final static String NEXT_HANDLER = "";
    private final static String PREVIOUS_HANDLER = SaveAudioHandler.CODE;
    private final static String CLASS_NAME = ShareAudioHandler.class.getSimpleName();
    private final AudioService audioService;
    private final ExecutorService executorService;
    private final PreviousService previousService;
    private final StatsService statsService;

    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (event.getPrevious_step().equals(PREVIOUS_HANDLER) && event.getCode().equals(CODE) && event.isHasCallbackQuery()) {
                Long chatId = event.getChatId();
                if (event.getData().equals(SaveAudioHandler.BUTTON_SHARE)) {
                    audioService.changeIsPrivateFlag(true, previousService.getData(chatId));
                    savePreviousStep(getPreviousStep(chatId), CLASS_NAME);
                    recordStats(getStats(chatId));
                    return Flux.just(sendMessage(chatId, "Great, the world will hear your voice"));
                } else {
                    savePreviousStep(getPreviousStep(chatId), CLASS_NAME);
                    recordStats(getStats(chatId));
                    return Flux.just(sendMessage(chatId, "OK, the message will remain private"));
                }
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
    }

    private void savePreviousAndStats(Long chatId) {
        savePreviousStep(getPreviousStep(chatId), CLASS_NAME);
        recordStats(getStats(chatId));
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
